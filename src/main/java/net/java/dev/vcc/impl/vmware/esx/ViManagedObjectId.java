package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.ManagedObjectReference;
import net.java.dev.vcc.api.ManagedObject;
import net.java.dev.vcc.api.ManagedObjectId;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Jul 31, 2009 Time: 12:16:57 PM To change this template use File |
 * Settings | File Templates.
 */
class ViManagedObjectId<T extends ManagedObject> extends ManagedObjectId<T> {

    /**
     * The managed object reference of this
     */
    private transient ManagedObjectReference moRef;


    private static final long serialVersionUID = 1L;
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("type", String.class),
            new ObjectStreamField("value", String.class),
    };

    public ViManagedObjectId(Class<T> managedObjectClass, ViDatacenterId datacenterId, ManagedObjectReference mo) {
        super(managedObjectClass, datacenterId.getDatacenterUrl());
        moRef = mo;
    }

    ManagedObjectReference getMORef() {
        return moRef;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("type", moRef.getType());
        fields.put("value", moRef.getValue());
        out.writeFields();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        moRef = new ManagedObjectReference();
        moRef.setType((String) fields.get("type", null));
        moRef.setValue((String) fields.get("value", null));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ViManagedObjectId)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ViManagedObjectId that = (ViManagedObjectId) o;

        if (!moRef.equals(that.moRef)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + moRef.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        try {
            sb.append(";type=");
            sb.append(URLEncoder.encode(moRef.getType(), "UTF-8"));
            sb.append(";value=");
            sb.append(URLEncoder.encode(moRef.getValue(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // ignore, this cannot happen
        }
        return sb.toString();
    }
}
