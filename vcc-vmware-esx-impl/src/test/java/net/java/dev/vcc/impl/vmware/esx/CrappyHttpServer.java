package net.java.dev.vcc.impl.vmware.esx;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very basic HTTP server that just captures any requests and sends a pre-canned response
 */
public class CrappyHttpServer implements Runnable {

    private ServerSocket socket;

    private volatile boolean stop = false;

    private final Lock lock = new ReentrantLock();

    private final Condition haveRequest = lock.newCondition();

    private byte[] lastRequest = null;

    private Properties lastHeaders = null;

    private Map<String, Response> responses = new HashMap<String, Response>();

    private static final Response DEFAULT_RESPONSE = new Response("application/soap+xml; charset=utf-8", createDefaultResponseContent());


    public boolean isRequestAvailable() {
        lock.lock();
        try {
            return lastRequest != null;
        } finally {
            lock.unlock();
        }
    }

    public byte[] getRequest() {
        lock.lock();
        try {
            return lastRequest;
        } finally {
            lock.unlock();
        }
    }

    public Properties getRequestHeaders() {
        lock.lock();
        try {
            return lastHeaders;
        } finally {
            lock.unlock();
        }
    }

    public void clearRequest() {
        lock.lock();
        try {
            lastRequest = null;
            lastHeaders = null;
        } finally {
            lock.unlock();
        }
    }

    public boolean awaitRequest(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            return haveRequest.await(timeout, unit);
        } finally {
            lock.unlock();
        }
    }

    public CrappyHttpServer(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public void shutdown() {
        stop = true;
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public void addResponse(String httpMethod, String path, String contentType, byte[] content) {
        responses.put(httpMethod + " " + path, new Response(contentType, content));
    }

    public void run() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Listening for incoming connections on {0}",
                socket.getLocalSocketAddress());
        try {
            socket.setSoTimeout(100);
            while (!stop) {
                try {
                    Socket client = socket.accept();
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "Incoming connection from {0}",
                            client.getRemoteSocketAddress());
                    String request = null;
                    Properties headers = null;
                    byte[] content = null;
                    InputStream inputStream = client.getInputStream();
                    try {
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        try {
                            request = readRequest(bis);
                            headers = parseHeader(readHeader(bis));
                            content = readContent(bis, headers);
                            lock.lock();
                            try {
                                if (lastRequest == null) {
                                    lastHeaders = headers;
                                    lastRequest = content;
                                    haveRequest.signalAll();
                                }
                            } finally {
                                lock.unlock();
                            }
                            Logger.getLogger(getClass().getName()).log(Level.FINE, "Recieved request from {0} for {3}\nHeaders:\n--------\n{1}\n\nBody:\n-----\n{2}\n\n",
                                    new Object[]{client.getRemoteSocketAddress(), headers, new String(content, "UTF-8"), request});
                        } finally {
                            bis.close();
                        }
                    } finally {
                        inputStream.close();
                    }

                    Response response = DEFAULT_RESPONSE;
                    if (request != null) {
                        int methodEnd = request.indexOf(' ');
                        int pathEnd = request.indexOf(' ', methodEnd + 1);
                        String key = request.substring(0, methodEnd).toUpperCase() + " " +
                                request.substring(methodEnd + 1, pathEnd);
                        if (responses.containsKey(key)) {
                            response = responses.get(key);
                        }
                    }

                    OutputStream outputStream = client.getOutputStream();
                    try {
                        byte[] responseContent = response.getContent();
                        byte[] responseHeader = createResponseHeader(response.getContentType(), responseContent);

                        outputStream.write(responseHeader);
                        outputStream.write(responseContent);
                        outputStream.write(0);
                    } finally {
                        outputStream.close();
                    }

                    client.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private String readRequest(InputStream is) throws IOException {
        ByteArrayOutputStream request = new ByteArrayOutputStream();
        int b;
        while (-1 != (b = is.read())) {
            request.write(b);
            if (b == 13 || b == 10) {
                break;
            }
        }
        return new String(request.toByteArray(), "UTF-8");
    }

    private byte[] readContent(BufferedInputStream bis, Properties headers) throws IOException {
        String strLength = headers.getProperty("Content-Length", "0");
        int length = Integer.parseInt(strLength);
        byte[] content = new byte[length];
        bis.read(content);
        return content;
    }

    private Properties parseHeader(InputStream headerInputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(headerInputStream));

        Properties headers = new Properties();

        String line;
        while (null != (line = r.readLine())) {
            int index = line.indexOf(':');
            if (index > 0) {
                headers.setProperty(line.substring(0, index).trim(), line.substring(index + 1).trim());
            }
        }
        return headers;
    }

    private InputStream readHeader(InputStream bis) throws IOException {
        ByteArrayOutputStream header = new ByteArrayOutputStream();
        int state = 1; // because we at least had a CR from the request
        int b;
        while (state != 4 && -1 != (b = bis.read())) {
            header.write(b);
            if (state != 0 && b != 13 && b != 10) {
                // reset as header terminator is CRLFCRLF (or LFLF)
                state = 0;
            } else if (state == 0 && b == 13) {
                state = 1;
            } else if ((state == 0 || state == 1) && b == 10) {
                state = 2;
            } else if (state == 2 && b == 13) {
                state = 3;
            } else if ((state == 2 || state == 3) && b == 10) {
                state = 4;
            }
        }
        return new ByteArrayInputStream(header.toByteArray());
    }

    private byte[] createResponseHeader(String contentType, byte[] content) throws UnsupportedEncodingException {
        StringBuilder buf = new StringBuilder();
        buf.append("HTTP/1.1 200 OK\r\n");
        buf.append("Content-Length: ");
        buf.append(content.length);
        buf.append("\r\n");
        buf.append("Connection: close\r\n");
        buf.append("Content-Type: ");
        buf.append(contentType);
        buf.append("\r\n");
        buf.append("\r\n");
        return buf.toString().getBytes("UTF-8");
    }

    private static byte[] createDefaultResponseContent() {
        StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\"?>\n" +
                "<SOAP-ENV:Envelope\n" +
                "  xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "  xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"\n" +
                "  xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n" +
                "   <SOAP-ENV:Body>\n" +
                "     <SOAP-ENV:Fault>\n" +
                "     <faultcode xsi:type=\"xsd:string\">SOAP-ENV:Client</faultcode>\n" +
                "     <faultstring xsi:type=\"xsd:string\">\n" +
                "          I am not a real server.\n" +
                "        </faultstring>\n" +
                "      </SOAP-ENV:Fault>\n" +
                "  </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n");
        try {
            return buf.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return buf.toString().getBytes();
        }
    }

    public static void main(String[] args) throws IOException {
        new CrappyHttpServer(8080).run();
    }

    private static final class Response {
        private final String contentType;
        private final byte[] content;

        private Response(String contentType, byte[] content) {
            this.contentType = contentType;
            this.content = content;
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
