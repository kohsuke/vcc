package net.java.dev.vcc.impl.vmware.esx.vim25;

import com.vmware.vim25.Event;
import com.vmware.vim25.EventFilterSpec;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.VimPortType;
import net.java.dev.vcc.impl.vmware.esx.CrappyHttpServer;
import net.java.dev.vcc.impl.vmware.esx.Environment;
import net.java.dev.vcc.impl.vmware.esx.JavaBeanHelper;
import net.java.dev.vcc.impl.vmware.esx.StringContainsMatcher;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Basic tests
 */
public class SmokeTest {

    private static final String URL = Environment.getUrl();

    private static final String USERNAME = Environment.getUsername();

    private static final String PASSWORD = Environment.getPassword();

    @Test
    public void smokeTest() throws Exception {

        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

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

    @Test
    public void watchEvents() throws Exception {
        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        final VimPortType proxy = ConnectionManager.getConnection(URL);
        final ManagedObjectReference serviceInstance = ConnectionManager.getServiceInstance();

        ServiceContent serviceContent = proxy.retrieveServiceContent(serviceInstance);
        ManagedObjectReference sessionManager = serviceContent.getSessionManager();
        UserSession session = proxy.login(sessionManager, USERNAME, PASSWORD, null);
        try {
            ManagedObjectReference eventManager = serviceContent.getEventManager();
            EventFilterSpec eventFilter = new EventFilterSpec();
            ManagedObjectReference eventHistoryCollector = proxy.createCollectorForEvents(eventManager, eventFilter);

            PropertySpec propSpec = new PropertySpec();
            propSpec.setAll(false);
            propSpec.setPathSet(Arrays.asList("latestPage"));
            propSpec.setType(eventHistoryCollector.getType());

            ObjectSpec objSpec = new ObjectSpec();
            objSpec.setObj(eventHistoryCollector);
            objSpec.setSkip(false);
            objSpec.setSelectSet(Collections.<SelectionSpec>emptyList());
            PropertyFilterSpec spec = new PropertyFilterSpec();
            spec.setObjectSet(Arrays.asList(objSpec));
            spec.setPropSet(Arrays.asList(propSpec));

            proxy.resetCollector(eventHistoryCollector);
            long finished = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(300);
            while (System.currentTimeMillis() < finished) {
                System.out.println("\n\n*** " + new Date() + " ***\n");
                boolean more;
                do {
                    more = false;
                    List<Event> list = proxy.readNextEvents(eventHistoryCollector, 100);
                    for (Event event : list) {
                        JavaBeanHelper.describe(event);
                        more = true;
                    }
                } while (more);
                TimeUnit.SECONDS.sleep(5);
            }
        } finally {
            proxy.logout(sessionManager);
        }
    }

    @Test
    public void listVirtualComputers() throws Exception {

        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        final VimPortType proxy = ConnectionManager.getConnection(URL);
        final ManagedObjectReference serviceInstance = ConnectionManager.getServiceInstance();

        ServiceContent serviceContent = proxy.retrieveServiceContent(serviceInstance);
        ManagedObjectReference sessionManager = serviceContent.getSessionManager();
        UserSession session = proxy.login(sessionManager, USERNAME, PASSWORD, null);
        try {
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
                if ("VirtualMachine".equals(c.getObj().getType())) {
                    System.out.println("Virtual Computer: " + Helper.asMap(c.getPropSet()).get("name"));
                } else {
                    System.out.println("***" + c.getObj().getType());
                }
            }

        } finally {
            proxy.logout(sessionManager);
        }
    }

    @Test
    public void jaxwsSendsTheFullRequest() throws Exception {
        CrappyHttpServer server = new CrappyHttpServer(8080);
        Thread thread = new Thread(server);
        thread.start();
        try {
            final VimPortType proxy = ConnectionManager
                    .getConnection("http://localhost:" + server.getLocalPort() + "/sdk");
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

            final PropertyFilterSpec spec = Helper.newPropertyFilterSpec(meName,
                    Helper.newObjectSpec(new ManagedObjectReference(), false, folderTraversalSpec));

            Thread requestMaker = new Thread() {
                public void run() {
                    try {
                        proxy.retrieveProperties(new ManagedObjectReference(), Arrays.asList(spec));
                    } catch (Throwable e) {
                        // ignore it's only the crappy server
                    }
                }
            };
            requestMaker.setDaemon(true);

            server.clearRequest();
            requestMaker.start();
            assertThat("The request maker made a request", server.awaitRequest(1, TimeUnit.SECONDS), is(true));
            requestMaker.interrupt();
            assertThat(new String(server.getRequest()), new StringContainsMatcher("computeResourceRpTraversalSpec"));
        } finally {
            server.shutdown();
            thread.join();
        }
    }
}
