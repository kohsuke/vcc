package net.java.dev.vcc.impl.vmware.esx.vim25;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.spi.LogFactoryManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;

public class ConnectionManager {


    public static VimPortType getConnection(String url, Executor executor) throws MalformedURLException {
        final Log LOGGER = LogFactoryManager.getLogFactory().getLog(ConnectionManager.class);
        VimService vimService = new VimService(VimService.class.getResource("vimService.wsdl"),
                new QName("urn:vim25Service", "VimService")
        );
        vimService.setExecutor(executor);
        final VimPortType proxy = vimService.getVimPort();
        final BindingProvider bindingProvider = (BindingProvider) proxy;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        bindingProvider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

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
            SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
            bindingProvider.getRequestContext()
                    .put("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", sslSocketFactory);
            bindingProvider.getRequestContext()
                    .put("com.sun.xml.ws.transport.https.client.SSLSocketFactory", sslSocketFactory);
        } catch (Exception e) {
            LOGGER.info(e, "Could not install the all-trusting socket factory");
        }

        // Install the all-trusting hostname verifier
        // VMware ESX servers typically do not have a cert with the correct hostname
        final HostnameVerifier verifier = new HostnameVerifier() {
            public boolean verify(String s, SSLSession sslSession) {
                LOGGER.debug("Verifying hostname: {0}", s);
                return true;
            }
        };

        bindingProvider.getRequestContext()
                .put("com.sun.xml.internal.ws.transport.https.client.hostname.verifier", verifier);
        bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.hostname.verifier", verifier);

        return proxy;
    }

    public static final ManagedObjectReference getServiceInstance() {
        ManagedObjectReference serviceInstance = new ManagedObjectReference();
        serviceInstance.setType("ServiceInstance");
        serviceInstance.setValue("ServiceInstance");
        return serviceInstance;
    }

}
