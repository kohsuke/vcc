package net.java.dev.vcc.impl.vmware.esx.vim25;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import com.vmware.vim25.ManagedObjectReference;
import com.sun.xml.ws.developer.JAXWSProperties;

public class ConnectionManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());

    public static VimPortType getConnection(String url) throws MalformedURLException {
        final URL wsdlUrl = url.endsWith("/") ? new URL(url + "vimService.wsdl"): new URL(url + "/vimService.wsdl");
        final VimPortType proxy =
                new VimService(wsdlUrl, new QName("urn:vim25Service", "VimService")).getVimPort();
        final BindingProvider bindingProvider = (BindingProvider) proxy;
        bindingProvider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url + "/vimService/");
        bindingProvider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        // Install the all-trusting hostname verifier
        // VMware ESX servers typically do not have a cert with the correct hostname
        bindingProvider.getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, new HostnameVerifier() {
            public boolean verify(String s, SSLSession sslSession) {
                System.out.println("verifying " + s);
                return true;
            }
        });
        // Install the all-trusting trust manager
        // VMware ESX servers typically have a self-signed cert with the correct hostname
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            }, new java.security.SecureRandom());
            bindingProvider.getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sc.getSocketFactory());
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Could not install the all-trusting socket factory", e);
        }

        return proxy;
    }

    public static final ManagedObjectReference getServiceInstance() {
        ManagedObjectReference serviceInstance = new ManagedObjectReference();
        serviceInstance.setType("ServiceInstance");
        serviceInstance.setValue("ServiceInstance");
        return serviceInstance;
    }

}
