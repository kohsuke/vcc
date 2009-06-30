package net.java.dev.vcc.impl.vmware.esx.vim25;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.VimPortType;
import net.java.dev.vcc.impl.vmware.esx.JavaBeanHelper;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * Basic tests
 */
public class SmokeTest {

    private static final String URL = "http or https://vmware esx or vmware server hostname/sdk";

    private static final String USERNAME = "please populate";

    private static final String PASSWORD = "please populate";

    @Ignore("Provide proper connection details")
    @Test
    public void smokeTest() throws Exception {

        final VimPortType proxy = ConnectionManager.getConnection(URL);
        final ManagedObjectReference serviceInstance = ConnectionManager.getServiceInstance();

        ServiceContent serviceContent = proxy.retrieveServiceContent(serviceInstance);
        ManagedObjectReference sessionManager = serviceContent.getSessionManager();
        UserSession session = proxy.login(sessionManager, USERNAME, PASSWORD, null);
        try {
            JavaBeanHelper.describe(session);
            JavaBeanHelper.describe(serviceContent);

            TraversalSpec resourcePoolTraversalSpec = Helper
                    .newTraversalSpec("resourcePoolTraversalSpec", "ResourcePool", "resourcePool", false,
                            Helper.newSelectionSpec("resourcePoolTraversalSpec"));

            TraversalSpec computeResourceRpTraversalSpec = Helper
                    .newTraversalSpec("computeResourceRpTraversalSpec", "ComputeResource", "resourcePool", false,
                            Helper.newSelectionSpec("resourcePoolTraversalSpec"));

            TraversalSpec computeResourceHostTraversalSpec = Helper
                    .newTraversalSpec("computeResourceHostTraversalSpec", "ComputeResource", "host", false);

            TraversalSpec datacenterHostTraversalSpec = Helper
                    .newTraversalSpec("datacenterHostTraversalSpec", "Datacenter", "hostFolder", false,
                            Helper.newSelectionSpec("folderTraversalSpec"));

            TraversalSpec datacenterVmTraversalSpec = Helper
                    .newTraversalSpec("datacenterVmTraversalSpec", "Datacenter", "vmFolder", false,
                            Helper.newSelectionSpec("folderTraversalSpec"));

            TraversalSpec folderTraversalSpec = Helper
                    .newTraversalSpec("folderTraversalSpec", "Folder", "childEntity", false,
                            Helper.newSelectionSpec("folderTraversalSpec"),
                            datacenterHostTraversalSpec,
                            datacenterVmTraversalSpec,
                            computeResourceRpTraversalSpec,
                            computeResourceHostTraversalSpec,
                            resourcePoolTraversalSpec);

            PropertySpec meName = Helper.newPropertySpec("ManagedEntity", false, "name");

            PropertyFilterSpec spec = Helper.newPropertyFilterSpec(meName,
                    Helper.newObjectSpec(serviceContent.getRootFolder(), false, folderTraversalSpec));

            for (ObjectContent c : proxy
                    .retrieveProperties(serviceContent.getPropertyCollector(), Arrays.asList(spec))) {
                JavaBeanHelper.describe(c);
            }

        } finally {
            proxy.logout(sessionManager);
        }
    }

}
