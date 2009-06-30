package net.java.dev.vcc.impl.vmware.esx.vim25;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TraversalSpec;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Jun 30, 2009 Time: 12:38:05 PM To change this template use File |
 * Settings | File Templates.
 */
public final class Helper {
    private Helper() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class");
    }

    public static PropertyFilterSpec newPropertyFilterSpec(PropertySpec pSpec, ObjectSpec oSpec) {
        PropertyFilterSpec pfSpec = new PropertyFilterSpec();
        if (pSpec != null) {
            pfSpec.getPropSet().add(pSpec);
        }
        if (oSpec != null) {
            pfSpec.getObjectSet().add(oSpec);
        }
        return pfSpec;
    }

    public static PropertyFilterSpec newPropertyFilterSpec(PropertySpec[] pSpec, ObjectSpec[] oSpec) {
        PropertyFilterSpec pfSpec = new PropertyFilterSpec();
        if (pSpec != null) {
            pfSpec.getPropSet().addAll(Arrays.asList(pSpec));
        }
        if (oSpec != null) {
            pfSpec.getObjectSet().addAll(Arrays.asList(oSpec));
        }
        return pfSpec;
    }

    public static ObjectSpec newObjectSpec(ManagedObjectReference object, Boolean skip, SelectionSpec... selectSet) {
        ObjectSpec oSpec = new ObjectSpec();
        if (object != null) {
            oSpec.setObj(object);
        }
        oSpec.setSkip(skip);
        oSpec.getSelectSet().addAll(Arrays.asList(selectSet));
        return oSpec;
    }

    public static SelectionSpec newSelectionSpec(String name, String dynamicType, DynamicProperty... dynamicProperties
    ) {
        SelectionSpec spec = new SelectionSpec();
        if (name != null) {
            spec.setName(name);
        }
        if (dynamicType != null) {
            spec.setDynamicType(dynamicType);
        }
        spec.getDynamicProperty().addAll(Arrays.asList(dynamicProperties));
        return spec;
    }

    public static SelectionSpec newSelectionSpec(String name) {
        return newSelectionSpec(name, null);
    }

    public static TraversalSpec newTraversalSpec(String name, String type, String path, Boolean skip,
                                                 SelectionSpec... selectSet) {
        TraversalSpec spec = new TraversalSpec();
        if (name != null) {
            spec.setName(name);
        }
        if (type != null) {
            spec.setType(type);
        }
        if (path != null) {
            spec.setPath(path);
        }
        spec.setSkip(skip);
        spec.getSelectSet().addAll(Arrays.asList(selectSet));
        return spec;
    }

    public static PropertySpec newPropertySpec(String type, boolean all, String... pathSets) {
        PropertySpec pSpec = new PropertySpec();
        pSpec.setAll(all);
        if (type != null) {
            pSpec.setType(type);
        }
        pSpec.getPathSet().addAll(Arrays.asList(pathSets));
        return pSpec;
    }
}
