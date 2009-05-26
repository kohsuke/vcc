package net.java.dev.vcc.api;

import java.io.Serializable;

/**
 * The unique ID of a {@link net.java.dev.vcc.api.ManagedObject} instance.
 *
 * @param <T> The type of {@link net.java.dev.vcc.api.ManagedObject} that this ID is for.
 */
public abstract class ManagedObjectId<T extends ManagedObject> implements Serializable {

    /**
     * The class of {@link ManagedObject} that this ID is for.
     */
    private final Class<T> managedObjectClass;

    /**
     * The URL of the {@link Datacenter} hosting the instance referenced by this ID.
     */
    private final String datacenterUrl;

    /**
     * Constructs a new {@link net.java.dev.vcc.api.ManagedObjectId}.
     *
     * @param managedObjectClass The class of {@link ManagedObject} that this ID is for.
     * @param datacenterUrl      The URL of the {@link Datacenter} hosting the instance referenced by this ID.
     */
    protected ManagedObjectId(Class<T> managedObjectClass, String datacenterUrl) {
        this.managedObjectClass = managedObjectClass;
        this.datacenterUrl = datacenterUrl;
    }

    /**
     * Gets the class of {@link ManagedObject} that this ID is for.
     *
     * @return The class of {@link ManagedObject} that this ID is for.
     */
    public Class<T> getManagedObjectClass() {
        return managedObjectClass;
    }

    /**
     * Getst the URL of the {@link Datacenter} hosting the instance referenced by this ID.
     *
     * @return The URL of the {@link Datacenter} hosting the instance referenced by this ID.
     */
    public String getDatacenterUrl() {
        return datacenterUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ManagedObjectId that = (ManagedObjectId) o;

        if (!datacenterUrl.equals(that.datacenterUrl)) {
            return false;
        }
        if (!managedObjectClass.equals(that.managedObjectClass)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = managedObjectClass.hashCode();
        result = 31 * result + datacenterUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return datacenterUrl + ";class=" + managedObjectClass;
    }
}
