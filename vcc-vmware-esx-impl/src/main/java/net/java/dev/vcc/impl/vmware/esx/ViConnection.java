package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.*;
import net.java.dev.vcc.impl.vmware.esx.vim25.ConnectionManager;

import java.net.MalformedURLException;

/**
 * Holds the connection to a VMware ESX server.
 */
final class ViConnection {
    private final VimPortType proxy;
    private final ManagedObjectReference sessionManager;
    private final UserSession session;
    private final ManagedObjectReference serviceInstance;
    private final ServiceContent serviceContent;

    public ViConnection(String url, String username, char[] password) throws MalformedURLException, RuntimeFaultFaultMsg, InvalidLocaleFaultMsg, InvalidLoginFaultMsg {
        proxy = ConnectionManager.getConnection(url);
        serviceInstance = ConnectionManager.getServiceInstance();
        serviceContent = proxy.retrieveServiceContent(serviceInstance);
        sessionManager = serviceContent.getSessionManager();
        session = proxy.login(sessionManager, username, new String(password), null);
    }

    public VimPortType getProxy() {
        return proxy;
    }

    public ManagedObjectReference getSessionManager() {
        return sessionManager;
    }

    public UserSession getSession() {
        return session;
    }

    public ManagedObjectReference getServiceInstance() {
        return serviceInstance;
    }

    public ServiceContent getServiceContent() {
        return serviceContent;
    }
}
