package net.java.dev.vcc.impl.vmware.esx.vim25;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.Event;
import com.vmware.vim25.EventFilterSpec;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        final VimPortType proxy = ConnectionManager.getConnection(URL, null);
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

            PropertyFilterSpec spec = Helper.newPropertyFilterSpec(
                    new PropertySpec[]{meName, Helper.newPropertySpec("ManagedEntity", false, "parent")},
                    new ObjectSpec[]{Helper.newObjectSpec(serviceContent.getRootFolder(), false, folderTraversalSpec)});

            for (ObjectContent c : proxy
                    .retrieveProperties(serviceContent.getPropertyCollector(), Arrays.asList(spec))) {
                JavaBeanHelper.describe(c);
            }

        } finally {
            proxy.logout(sessionManager);
        }
    }

    @Test
    public void buildTree() throws Exception {

        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        final VimPortType proxy = ConnectionManager.getConnection(URL, null);
        final ManagedObjectReference serviceInstance = ConnectionManager.getServiceInstance();

        ServiceContent serviceContent = proxy.retrieveServiceContent(serviceInstance);
        ManagedObjectReference sessionManager = serviceContent.getSessionManager();
        UserSession session = proxy.login(sessionManager, USERNAME, PASSWORD, null);
        try {

            TraversalSpec folderTraversalSpec = Helper
                    .newTraversalSpec("folderTraversalSpec", "Folder", "childEntity", false,
                            Helper.newSelectionSpec("folderTraversalSpec"),
                            Helper.newTraversalSpec("datacenterHostTraversalSpec", "Datacenter", "hostFolder", false,
                                    Helper.newSelectionSpec("folderTraversalSpec")),
                            Helper.newTraversalSpec("datacenterVmTraversalSpec", "Datacenter", "vmFolder", false,
                                    Helper.newSelectionSpec("folderTraversalSpec")),
                            Helper.newTraversalSpec("computeResourceRpTraversalSpec", "ComputeResource", "resourcePool",
                                    false,
                                    Helper.newSelectionSpec("resourcePoolTraversalSpec")),
                            Helper.newTraversalSpec("computeResourceHostTraversalSpec", "ComputeResource", "host",
                                    false),
                            Helper.newTraversalSpec("resourcePoolTraversalSpec", "ResourcePool", "resourcePool", false,
                                    Helper.newSelectionSpec("resourcePoolTraversalSpec")));

            PropertyFilterSpec spec = Helper.newPropertyFilterSpec(
                    new PropertySpec[]{Helper.newPropertySpec("ManagedEntity", false, "name"),
                            Helper.newPropertySpec("ManagedEntity", false, "parent"),
                            Helper.newPropertySpec("VirtualMachine", false, "resourcePool"),
                    },
                    new ObjectSpec[]{Helper.newObjectSpec(serviceContent.getRootFolder(), false, folderTraversalSpec)});

            Map<String, MOHolder> tree = new HashMap<String, MOHolder>();
            tree.put(serviceContent.getRootFolder().getValue(), new MOHolder(serviceContent.getRootFolder(), "ROOT"));
            Map<String, Collection<MOHolder>> waiting = new HashMap<String, Collection<MOHolder>>();
            for (ObjectContent c : proxy
                    .retrieveProperties(serviceContent.getPropertyCollector(), Arrays.asList(spec))) {
                if (tree.containsKey(c.getObj().getValue())) {
                    continue;
                }
                MOHolder cHolder = new MOHolder(c.getObj(), (String) Helper.getDynamicProperty(c, "name"));
                ManagedObjectReference parent = (ManagedObjectReference) Helper.getDynamicProperty(c, "resourcePool");
                if (parent == null) {
                    parent = (ManagedObjectReference) Helper.getDynamicProperty(c, "parent");
                }
                if (parent != null) {
                    MOHolder parentHolder = tree.get(parent.getValue());
                    if (parentHolder != null) {
                        parentHolder.getChildren().add(cHolder);
                    } else {
                        Collection<MOHolder> _waiting = waiting.get(parent.getValue());
                        if (_waiting == null) {
                            waiting.put(parent.getValue(), _waiting = new ArrayList<MOHolder>());
                        }
                        _waiting.add(cHolder);
                    }
                }
                Collection<MOHolder> _waiting = waiting.get(c.getObj().getValue());
                if (_waiting != null) {
                    cHolder.getChildren().addAll(_waiting);
                    waiting.remove(c.getObj().getValue());
                }
                tree.put(c.getObj().getValue(), cHolder);

            }
            System.out.println(tree.get(serviceContent.getRootFolder().getValue()));

        } finally {
            proxy.logout(sessionManager);
        }
    }

    private static class MOHolder {
        private final ManagedObjectReference mo;
        private final String name;
        private final Collection<MOHolder> children = new ArrayList<MOHolder>();

        public MOHolder(ManagedObjectReference mo, String name) {
            this.name = name;
            mo.getClass();
            this.mo = mo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MOHolder moHolder = (MOHolder) o;

            if (!mo.equals(moHolder.mo)) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return toString("");
        }

        public String toString(String indent) {
            final StringBuilder sb = new StringBuilder();
            sb.append('\n');
            sb.append(indent);
            sb.append(mo.getValue());
            sb.append('[');
            sb.append(mo.getType());
            sb.append("] \"");
            sb.append(name);
            sb.append('\"');
            for (MOHolder child : children) {
                sb.append(child.toString(indent.replace('-', ' ') + " |--"));
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            return mo.hashCode();
        }

        public ManagedObjectReference getMo() {
            return mo;
        }

        public Collection<MOHolder> getChildren() {
            return children;
        }

    }

    @Ignore("Long running")
    @Test
    public void watchEvents() throws Exception {
        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        final VimPortType proxy = ConnectionManager.getConnection(URL, null);
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
    public void navigate() throws Exception {
        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        final VimPortType proxy = ConnectionManager.getConnection(URL, null);
        final ManagedObjectReference serviceInstance = ConnectionManager.getServiceInstance();

        ServiceContent serviceContent = proxy.retrieveServiceContent(serviceInstance);
        ManagedObjectReference sessionManager = serviceContent.getSessionManager();
        UserSession session = proxy.login(sessionManager, USERNAME, PASSWORD, null);
        try {
            ManagedObjectReference rootFolder = serviceContent.getRootFolder();
            listChildren("    ", proxy, serviceContent, rootFolder, "childEntity", "hostFolder", "childEntity",
                    "resourcePool", "vm");
        } finally {
            proxy.logout(sessionManager);
        }
    }

    private void listChildren(String depth, VimPortType proxy, ServiceContent serviceContent,
                              ManagedObjectReference parent,
                              String... path)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        if (path.length == 0) {
            return;
        }
        String[] childPath = new String[path.length - 1];
        System.arraycopy(path, 1, childPath, 0, childPath.length);
        Map<String, ManagedObjectReference> c1 = getChildEntities(proxy, serviceContent, parent, parent.getType(),
                path[0]
        );
        for (ManagedObjectReference mp : c1.values()) {
            if (mp.getValue().equals(parent.getValue())) {
                continue;
            }
            System.out.println(depth + mp.getType() + " -> " + mp.getValue());
            try {
                listChildren("    " + depth, proxy, serviceContent, mp, childPath);
            } catch (InvalidPropertyFaultMsg e) {
                // ignore
            }
        }
    }


    private Map<String, ManagedObjectReference> getChildEntities(VimPortType portType, ServiceContent serviceContent,
                                                                 ManagedObjectReference rootFolder, String type,
                                                                 String path
    )
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        Map<String, ManagedObjectReference> children = new HashMap<String, ManagedObjectReference>();
        PropertyFilterSpec spec = Helper.newPropertyFilterSpec(Helper.newPropertySpec("ManagedEntity", false, "name"),
                Helper.newObjectSpec(rootFolder, false, Helper
                        .newTraversalSpec("folderTraversalSpec", type, path, false,
                        Helper.newSelectionSpec("folderTraversalSpec"))));
        for (ObjectContent c : portType
                .retrieveProperties(serviceContent.getPropertyCollector(), Arrays.asList(spec))) {
            String name = null;
            for (DynamicProperty p : c.getPropSet()) {
                if ("name".equals(p.getName()) && p.getVal() instanceof String) {
                    name = (String) p.getVal();
                    break;
                }
            }
            if (name != null) {
                children.put(name, c.getObj());
            }
        }
        return children;
    }

    @Test
    public void listVirtualComputers() throws Exception {

        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        final VimPortType proxy = ConnectionManager.getConnection(URL, null);
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
        CrappyHttpServer server = new CrappyHttpServer(0);
        Thread thread = new Thread(server);
        thread.start();
        try {
            final VimPortType proxy = ConnectionManager
                    .getConnection("http://localhost:" + server.getLocalPort() + "/sdk", null);
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
