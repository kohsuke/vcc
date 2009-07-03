package net.java.dev.vcc.impl.vmware.esx;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    public void run() {
        try {
            socket.setSoTimeout(100);
            while (!stop) {
                try {
                    Socket client = socket.accept();
                    InputStream inputStream = client.getInputStream();
                    try {
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        try {
                            Properties headers = parseHeader(readHeader(bis));
                            byte[] content = readContent(bis, headers);
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
                        } finally {
                            bis.close();
                        }
                    } finally {
                        inputStream.close();
                    }


                    OutputStream outputStream = client.getOutputStream();
                    try {
                        byte[] responseContent = createResponseContent();
                        byte[] responseHeader = createResponseHeader(responseContent);
                        
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
        r.readLine(); // ignore the request line

        String line;
        while (null != (line = r.readLine()) && line.trim().length() > 0) {
            int index = line.indexOf(':');
            headers.setProperty(line.substring(0, index).trim(), line.substring(index + 1).trim());
        }
        return headers;
    }

    private InputStream readHeader(InputStream bis) throws IOException {
        ByteArrayOutputStream header = new ByteArrayOutputStream();
        int state = 0;
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

    private byte[] createResponseHeader(byte[] content) throws UnsupportedEncodingException {
        StringBuilder buf = new StringBuilder();
        buf.append("HTTP/1.1 200 OK\r\n");
        buf.append("Content-Length: ");
        buf.append(content.length);
        buf.append("\r\n");
        buf.append("Connection: close\r\n");
        buf.append("Content-Type: application/soap+xml; charset=utf-8\r\n");
        buf.append("\r\n");
        return buf.toString().getBytes("UTF-8");
    }

    private byte[] createResponseContent() throws UnsupportedEncodingException {
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
        return buf.toString().getBytes("UTF-8");
    }

    public static void main(String[] args) throws IOException {
        new CrappyHttpServer(8080).run();
    }
}
