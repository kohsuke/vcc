/*
 * Copyright (c) 2006-2008 Citrix Systems, Inc.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as published
 * by the Free Software Foundation, with the additional linking exception as
 * follows:
 * 
 *   Linking this library statically or dynamically with other modules is
 *   making a combined work based on this library. Thus, the terms and
 *   conditions of the GNU General Public License cover the whole combination.
 * 
 *   As a special exception, the copyright holders of this library give you
 *   permission to link this library with independent modules to produce an
 *   executable, regardless of the license terms of these independent modules,
 *   and to copy and distribute the resulting executable under terms of your
 *   choice, provided that you also meet, for each linked independent module,
 *   the terms and conditions of the license of that module. An independent
 *   module is a module which is not derived from or based on this library. If
 *   you modify this library, you may extend this exception to your version of
 *   the library, but you are not obligated to do so. If you do not wish to do
 *   so, delete this exception statement from your version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.xensource.xenapi;

import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.VersionException;
import com.xensource.xenapi.Types.XenAPIException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Pool-wide information
 *
 * @author Citrix Systems, Inc.
 */
public class Pool extends XenAPIObject {

    /**
     * The XenAPI reference to this object.
     */
    protected final String ref;

    /**
     * For internal use only.
     */
    Pool(String ref) {
       this.ref = ref;
    }

    public String toWireString() {
       return this.ref;
    }

    /**
     * If obj is a Pool, compares XenAPI references for equality.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Pool)
        {
            Pool other = (Pool) obj;
            return other.ref.equals(this.ref);
        } else
        {
            return false;
        }
    }

    /**
     * Represents all the fields in a Pool
     */
    public static class Record implements Types.Record {
        public String toString() {
            StringWriter writer = new StringWriter();
            PrintWriter print = new PrintWriter(writer);
            print.printf("%1$20s: %2$s\n", "uuid", this.uuid);
            print.printf("%1$20s: %2$s\n", "nameLabel", this.nameLabel);
            print.printf("%1$20s: %2$s\n", "nameDescription", this.nameDescription);
            print.printf("%1$20s: %2$s\n", "master", this.master);
            print.printf("%1$20s: %2$s\n", "defaultSR", this.defaultSR);
            print.printf("%1$20s: %2$s\n", "suspendImageSR", this.suspendImageSR);
            print.printf("%1$20s: %2$s\n", "crashDumpSR", this.crashDumpSR);
            print.printf("%1$20s: %2$s\n", "otherConfig", this.otherConfig);
            print.printf("%1$20s: %2$s\n", "haEnabled", this.haEnabled);
            print.printf("%1$20s: %2$s\n", "haConfiguration", this.haConfiguration);
            print.printf("%1$20s: %2$s\n", "haStatefiles", this.haStatefiles);
            print.printf("%1$20s: %2$s\n", "haHostFailuresToTolerate", this.haHostFailuresToTolerate);
            print.printf("%1$20s: %2$s\n", "haPlanExistsFor", this.haPlanExistsFor);
            print.printf("%1$20s: %2$s\n", "haAllowOvercommit", this.haAllowOvercommit);
            print.printf("%1$20s: %2$s\n", "haOvercommitted", this.haOvercommitted);
            print.printf("%1$20s: %2$s\n", "blobs", this.blobs);
            print.printf("%1$20s: %2$s\n", "tags", this.tags);
            print.printf("%1$20s: %2$s\n", "guiConfig", this.guiConfig);
            return writer.toString();
        }

        /**
         * Convert a pool.Record to a Map
         */
        public Map<String,Object> toMap() {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("uuid", this.uuid == null ? "" : this.uuid);
            map.put("name_label", this.nameLabel == null ? "" : this.nameLabel);
            map.put("name_description", this.nameDescription == null ? "" : this.nameDescription);
            map.put("master", this.master == null ? new Host("OpaqueRef:NULL") : this.master);
            map.put("default_SR", this.defaultSR == null ? new SR("OpaqueRef:NULL") : this.defaultSR);
            map.put("suspend_image_SR", this.suspendImageSR == null ? new SR("OpaqueRef:NULL") : this.suspendImageSR);
            map.put("crash_dump_SR", this.crashDumpSR == null ? new SR("OpaqueRef:NULL") : this.crashDumpSR);
            map.put("other_config", this.otherConfig == null ? new HashMap<String, String>() : this.otherConfig);
            map.put("ha_enabled", this.haEnabled == null ? false : this.haEnabled);
            map.put("ha_configuration", this.haConfiguration == null ? new HashMap<String, String>() : this.haConfiguration);
            map.put("ha_statefiles", this.haStatefiles == null ? new LinkedHashSet<String>() : this.haStatefiles);
            map.put("ha_host_failures_to_tolerate", this.haHostFailuresToTolerate == null ? 0 : this.haHostFailuresToTolerate);
            map.put("ha_plan_exists_for", this.haPlanExistsFor == null ? 0 : this.haPlanExistsFor);
            map.put("ha_allow_overcommit", this.haAllowOvercommit == null ? false : this.haAllowOvercommit);
            map.put("ha_overcommitted", this.haOvercommitted == null ? false : this.haOvercommitted);
            map.put("blobs", this.blobs == null ? new HashMap<String, Blob>() : this.blobs);
            map.put("tags", this.tags == null ? new LinkedHashSet<String>() : this.tags);
            map.put("gui_config", this.guiConfig == null ? new HashMap<String, String>() : this.guiConfig);
            return map;
        }

        /**
         * unique identifier/object reference
         */
        public String uuid;
        /**
         * Short name
         */
        public String nameLabel;
        /**
         * Description
         */
        public String nameDescription;
        /**
         * The host that is pool master
         */
        public Host master;
        /**
         * Default SR for VDIs
         */
        public SR defaultSR;
        /**
         * The SR in which VDIs for suspend images are created
         */
        public SR suspendImageSR;
        /**
         * The SR in which VDIs for crash dumps are created
         */
        public SR crashDumpSR;
        /**
         * additional configuration
         */
        public Map<String, String> otherConfig;
        /**
         * true if HA is enabled on the pool, false otherwise
         */
        public Boolean haEnabled;
        /**
         * The current HA configuration
         */
        public Map<String, String> haConfiguration;
        /**
         * HA statefile VDIs in use
         */
        public Set<String> haStatefiles;
        /**
         * Number of host failures to tolerate before the Pool is declared to be overcommitted
         */
        public Long haHostFailuresToTolerate;
        /**
         * Number of future host failures we have managed to find a plan for. Once this reaches zero any future host failures will cause the failure of protected VMs.
         */
        public Long haPlanExistsFor;
        /**
         * If set to false then operations which would cause the Pool to become overcommitted will be blocked.
         */
        public Boolean haAllowOvercommit;
        /**
         * True if the Pool is considered to be overcommitted i.e. if there exist insufficient physical resources to tolerate the configured number of host failures
         */
        public Boolean haOvercommitted;
        /**
         * Binary blobs associated with this pool
         */
        public Map<String, Blob> blobs;
        /**
         * user-specified tags for categorization purposes
         */
        public Set<String> tags;
        /**
         * gui-specific configuration for pool
         */
        public Map<String, String> guiConfig;
    }

    /**
     * Get a record containing the current state of the given pool.
     *
     * @return all fields from the object
     */
    public Pool.Record getRecord(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_record";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toPoolRecord(result);
    }

    /**
     * Get a reference to the pool instance with the specified UUID.
     *
     * @param uuid UUID of object to return
     * @return reference to the object
     */
    public static Pool getByUuid(Connection c, String uuid) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_by_uuid";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(uuid)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toPool(result);
    }

    /**
     * Get the uuid field of the given pool.
     *
     * @return value of the field
     */
    public String getUuid(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_uuid";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toString(result);
    }

    /**
     * Get the name_label field of the given pool.
     *
     * @return value of the field
     */
    public String getNameLabel(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_name_label";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toString(result);
    }

    /**
     * Get the name_description field of the given pool.
     *
     * @return value of the field
     */
    public String getNameDescription(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_name_description";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toString(result);
    }

    /**
     * Get the master field of the given pool.
     *
     * @return value of the field
     */
    public Host getMaster(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_master";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toHost(result);
    }

    /**
     * Get the default_SR field of the given pool.
     *
     * @return value of the field
     */
    public SR getDefaultSR(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_default_SR";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSR(result);
    }

    /**
     * Get the suspend_image_SR field of the given pool.
     *
     * @return value of the field
     */
    public SR getSuspendImageSR(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_suspend_image_SR";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSR(result);
    }

    /**
     * Get the crash_dump_SR field of the given pool.
     *
     * @return value of the field
     */
    public SR getCrashDumpSR(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_crash_dump_SR";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSR(result);
    }

    /**
     * Get the other_config field of the given pool.
     *
     * @return value of the field
     */
    public Map<String, String> getOtherConfig(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toMapOfStringString(result);
    }

    /**
     * Get the ha_enabled field of the given pool.
     *
     * @return value of the field
     */
    public Boolean getHaEnabled(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_enabled";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toBoolean(result);
    }

    /**
     * Get the ha_configuration field of the given pool.
     *
     * @return value of the field
     */
    public Map<String, String> getHaConfiguration(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_configuration";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toMapOfStringString(result);
    }

    /**
     * Get the ha_statefiles field of the given pool.
     *
     * @return value of the field
     */
    public Set<String> getHaStatefiles(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_statefiles";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSetOfString(result);
    }

    /**
     * Get the ha_host_failures_to_tolerate field of the given pool.
     *
     * @return value of the field
     */
    public Long getHaHostFailuresToTolerate(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_host_failures_to_tolerate";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toLong(result);
    }

    /**
     * Get the ha_plan_exists_for field of the given pool.
     *
     * @return value of the field
     */
    public Long getHaPlanExistsFor(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_plan_exists_for";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toLong(result);
    }

    /**
     * Get the ha_allow_overcommit field of the given pool.
     *
     * @return value of the field
     */
    public Boolean getHaAllowOvercommit(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_allow_overcommit";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toBoolean(result);
    }

    /**
     * Get the ha_overcommitted field of the given pool.
     *
     * @return value of the field
     */
    public Boolean getHaOvercommitted(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_ha_overcommitted";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toBoolean(result);
    }

    /**
     * Get the blobs field of the given pool.
     *
     * @return value of the field
     */
    public Map<String, Blob> getBlobs(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_blobs";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toMapOfStringBlob(result);
    }

    /**
     * Get the tags field of the given pool.
     *
     * @return value of the field
     */
    public Set<String> getTags(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_tags";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSetOfString(result);
    }

    /**
     * Get the gui_config field of the given pool.
     *
     * @return value of the field
     */
    public Map<String, String> getGuiConfig(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_gui_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toMapOfStringString(result);
    }

    /**
     * Set the name_label field of the given pool.
     *
     * @param nameLabel New value to set
     */
    public void setNameLabel(Connection c, String nameLabel) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_name_label";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(nameLabel)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the name_description field of the given pool.
     *
     * @param nameDescription New value to set
     */
    public void setNameDescription(Connection c, String nameDescription) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_name_description";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(nameDescription)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the default_SR field of the given pool.
     *
     * @param defaultSR New value to set
     */
    public void setDefaultSR(Connection c, SR defaultSR) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_default_SR";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(defaultSR)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the suspend_image_SR field of the given pool.
     *
     * @param suspendImageSR New value to set
     */
    public void setSuspendImageSR(Connection c, SR suspendImageSR) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_suspend_image_SR";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(suspendImageSR)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the crash_dump_SR field of the given pool.
     *
     * @param crashDumpSR New value to set
     */
    public void setCrashDumpSR(Connection c, SR crashDumpSR) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_crash_dump_SR";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(crashDumpSR)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the other_config field of the given pool.
     *
     * @param otherConfig New value to set
     */
    public void setOtherConfig(Connection c, Map<String, String> otherConfig) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(otherConfig)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Add the given key-value pair to the other_config field of the given pool.
     *
     * @param key Key to add
     * @param value Value to add
     */
    public void addToOtherConfig(Connection c, String key, String value) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.add_to_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Remove the given key and its corresponding value from the other_config field of the given pool.  If the key is not in that Map, then do nothing.
     *
     * @param key Key to remove
     */
    public void removeFromOtherConfig(Connection c, String key) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.remove_from_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the ha_allow_overcommit field of the given pool.
     *
     * @param haAllowOvercommit New value to set
     */
    public void setHaAllowOvercommit(Connection c, Boolean haAllowOvercommit) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_ha_allow_overcommit";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(haAllowOvercommit)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the tags field of the given pool.
     *
     * @param tags New value to set
     */
    public void setTags(Connection c, Set<String> tags) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_tags";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(tags)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Add the given value to the tags field of the given pool.  If the value is already in that Set, then do nothing.
     *
     * @param value New value to add
     */
    public void addTags(Connection c, String value) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.add_tags";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Remove the given value from the tags field of the given pool.  If the value is not in that Set, then do nothing.
     *
     * @param value Value to remove
     */
    public void removeTags(Connection c, String value) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.remove_tags";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Set the gui_config field of the given pool.
     *
     * @param guiConfig New value to set
     */
    public void setGuiConfig(Connection c, Map<String, String> guiConfig) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_gui_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(guiConfig)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Add the given key-value pair to the gui_config field of the given pool.
     *
     * @param key Key to add
     * @param value Value to add
     */
    public void addToGuiConfig(Connection c, String key, String value) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.add_to_gui_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Remove the given key and its corresponding value from the gui_config field of the given pool.  If the key is not in that Map, then do nothing.
     *
     * @param key Key to remove
     */
    public void removeFromGuiConfig(Connection c, String key) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.remove_from_gui_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Instruct host to join a new pool
     *
     * @param masterAddress The hostname of the master of the pool to join
     * @param masterUsername The username of the master (for initial authentication)
     * @param masterPassword The password for the master (for initial authentication)
     * @return Task
     */
    public static Task joinAsync(Connection c, String masterAddress, String masterUsername, String masterPassword) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException,
       Types.JoiningHostCannotContainSharedSrs {
        String method_call = "Async.pool.join";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(masterAddress), Marshalling.toXMLRPC(masterUsername), Marshalling.toXMLRPC(masterPassword)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Instruct host to join a new pool
     *
     * @param masterAddress The hostname of the master of the pool to join
     * @param masterUsername The username of the master (for initial authentication)
     * @param masterPassword The password for the master (for initial authentication)
     */
    public static void join(Connection c, String masterAddress, String masterUsername, String masterPassword) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException,
       Types.JoiningHostCannotContainSharedSrs {
        String method_call = "pool.join";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(masterAddress), Marshalling.toXMLRPC(masterUsername), Marshalling.toXMLRPC(masterPassword)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Instruct host to join a new pool
     *
     * @param masterAddress The hostname of the master of the pool to join
     * @param masterUsername The username of the master (for initial authentication)
     * @param masterPassword The password for the master (for initial authentication)
     * @return Task
     */
    public static Task joinForceAsync(Connection c, String masterAddress, String masterUsername, String masterPassword) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.join_force";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(masterAddress), Marshalling.toXMLRPC(masterUsername), Marshalling.toXMLRPC(masterPassword)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Instruct host to join a new pool
     *
     * @param masterAddress The hostname of the master of the pool to join
     * @param masterUsername The username of the master (for initial authentication)
     * @param masterPassword The password for the master (for initial authentication)
     */
    public static void joinForce(Connection c, String masterAddress, String masterUsername, String masterPassword) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.join_force";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(masterAddress), Marshalling.toXMLRPC(masterUsername), Marshalling.toXMLRPC(masterPassword)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Instruct a pool master to eject a host from the pool
     *
     * @param host The host to eject
     * @return Task
     */
    public static Task ejectAsync(Connection c, Host host) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.eject";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(host)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Instruct a pool master to eject a host from the pool
     *
     * @param host The host to eject
     */
    public static void eject(Connection c, Host host) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.eject";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(host)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Instruct host that's currently a slave to transition to being master
     *
     */
    public static void emergencyTransitionToMaster(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.emergency_transition_to_master";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Instruct a slave already in a pool that the master has changed
     *
     * @param masterAddress The hostname of the master
     */
    public static void emergencyResetMaster(Connection c, String masterAddress) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.emergency_reset_master";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(masterAddress)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Instruct a pool master, M, to try and contact its slaves and, if slaves are in emergency mode, reset their master address to M.
     *
     * @return Task
     */
    public static Task recoverSlavesAsync(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.recover_slaves";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Instruct a pool master, M, to try and contact its slaves and, if slaves are in emergency mode, reset their master address to M.
     *
     * @return list of hosts whose master address were succesfully reset
     */
    public static Set<Host> recoverSlaves(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.recover_slaves";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSetOfHost(result);
    }

    /**
     * Create PIFs, mapping a network to the same physical interface/VLAN on each host. This call is deprecated: use Pool.create_VLAN_from_PIF instead.
     *
     * @param device physical interface on which to create the VLAN interface
     * @param network network to which this interface should be connected
     * @param VLAN VLAN tag for the new interface
     * @return Task
     */
    public static Task createVLANAsync(Connection c, String device, Network network, Long VLAN) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException,
       Types.VlanTagInvalid {
        String method_call = "Async.pool.create_VLAN";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(device), Marshalling.toXMLRPC(network), Marshalling.toXMLRPC(VLAN)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Create PIFs, mapping a network to the same physical interface/VLAN on each host. This call is deprecated: use Pool.create_VLAN_from_PIF instead.
     *
     * @param device physical interface on which to create the VLAN interface
     * @param network network to which this interface should be connected
     * @param VLAN VLAN tag for the new interface
     * @return The references of the created PIF objects
     */
    public static Set<PIF> createVLAN(Connection c, String device, Network network, Long VLAN) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException,
       Types.VlanTagInvalid {
        String method_call = "pool.create_VLAN";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(device), Marshalling.toXMLRPC(network), Marshalling.toXMLRPC(VLAN)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSetOfPIF(result);
    }

    /**
     * Create a pool-wide VLAN by taking the PIF.
     *
     * @param pif physical interface on any particular host, that identifies the PIF on which to create the (pool-wide) VLAN interface
     * @param network network to which this interface should be connected
     * @param VLAN VLAN tag for the new interface
     * @return Task
     */
    public static Task createVLANFromPIFAsync(Connection c, PIF pif, Network network, Long VLAN) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException,
       Types.VlanTagInvalid {
        String method_call = "Async.pool.create_VLAN_from_PIF";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(pif), Marshalling.toXMLRPC(network), Marshalling.toXMLRPC(VLAN)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Create a pool-wide VLAN by taking the PIF.
     *
     * @param pif physical interface on any particular host, that identifies the PIF on which to create the (pool-wide) VLAN interface
     * @param network network to which this interface should be connected
     * @param VLAN VLAN tag for the new interface
     * @return The references of the created PIF objects
     */
    public static Set<PIF> createVLANFromPIF(Connection c, PIF pif, Network network, Long VLAN) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException,
       Types.VlanTagInvalid {
        String method_call = "pool.create_VLAN_from_PIF";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(pif), Marshalling.toXMLRPC(network), Marshalling.toXMLRPC(VLAN)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSetOfPIF(result);
    }

    /**
     * Turn on High Availability mode
     *
     * @param heartbeatSrs Set of SRs to use for storage heartbeating.
     * @param configuration Detailed HA configuration to apply
     * @return Task
     */
    public static Task enableHaAsync(Connection c, Set<SR> heartbeatSrs, Map<String, String> configuration) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.enable_ha";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(heartbeatSrs), Marshalling.toXMLRPC(configuration)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Turn on High Availability mode
     *
     * @param heartbeatSrs Set of SRs to use for storage heartbeating.
     * @param configuration Detailed HA configuration to apply
     */
    public static void enableHa(Connection c, Set<SR> heartbeatSrs, Map<String, String> configuration) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.enable_ha";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(heartbeatSrs), Marshalling.toXMLRPC(configuration)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Turn off High Availability mode
     *
     * @return Task
     */
    public static Task disableHaAsync(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.disable_ha";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Turn off High Availability mode
     *
     */
    public static void disableHa(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.disable_ha";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Forcibly synchronise the database now
     *
     * @return Task
     */
    public static Task syncDatabaseAsync(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.sync_database";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Forcibly synchronise the database now
     *
     */
    public static void syncDatabase(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.sync_database";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Perform an orderly handover of the role of master to the referenced host.
     *
     * @param host The host who should become the new master
     * @return Task
     */
    public static Task designateNewMasterAsync(Connection c, Host host) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.designate_new_master";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(host)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Perform an orderly handover of the role of master to the referenced host.
     *
     * @param host The host who should become the new master
     */
    public static void designateNewMaster(Connection c, Host host) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.designate_new_master";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(host)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * When this call returns the VM restart logic will not run for the requested number of seconds. If the argument is zero then the restart thread is immediately unblocked
     *
     * @param seconds The number of seconds to block the restart thread for
     */
    public static void haPreventRestartsFor(Connection c, Long seconds) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.ha_prevent_restarts_for";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(seconds)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Returns true if a VM failover plan exists for up to 'n' host failures
     *
     * @param n The number of host failures to plan for
     * @return true if a failover plan exists for the supplied number of host failures
     */
    public static Boolean haFailoverPlanExists(Connection c, Long n) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.ha_failover_plan_exists";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(n)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toBoolean(result);
    }

    /**
     * Returns the maximum number of host failures we could tolerate before we would be unable to restart configured VMs
     *
     * @return maximum value for ha_host_failures_to_tolerate given current configuration
     */
    public static Long haComputeMaxHostFailuresToTolerate(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.ha_compute_max_host_failures_to_tolerate";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toLong(result);
    }

    /**
     * Returns the maximum number of host failures we could tolerate before we would be unable to restart the provided VMs
     *
     * @param configuration Map of protected VM reference to restart priority
     * @return maximum value for ha_host_failures_to_tolerate given provided configuration
     */
    public static Long haComputeHypotheticalMaxHostFailuresToTolerate(Connection c, Map<VM, String> configuration) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.ha_compute_hypothetical_max_host_failures_to_tolerate";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(configuration)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toLong(result);
    }

    /**
     * Return a VM failover plan assuming a given subset of hosts fail
     *
     * @param failedHosts The set of hosts to assume have failed
     * @param failedVms The set of VMs to restart
     * @return VM failover plan: a map of VM to host to restart the host on
     */
    public static Map<VM, Map<String, String>> haComputeVmFailoverPlan(Connection c, Set<Host> failedHosts, Set<VM> failedVms) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.ha_compute_vm_failover_plan";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(failedHosts), Marshalling.toXMLRPC(failedVms)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toMapOfVMMapOfStringString(result);
    }

    /**
     * Set the maximum number of host failures to consider in the HA VM restart planner
     *
     * @param value New number of host failures to consider
     * @return Task
     */
    public Task setHaHostFailuresToTolerateAsync(Connection c, Long value) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.set_ha_host_failures_to_tolerate";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Set the maximum number of host failures to consider in the HA VM restart planner
     *
     * @param value New number of host failures to consider
     */
    public void setHaHostFailuresToTolerate(Connection c, Long value) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.set_ha_host_failures_to_tolerate";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        return;
    }

    /**
     * Create a placeholder for a named binary blob of data that is associated with this pool
     *
     * @param name The name associated with the blob
     * @param mimeType The mime type for the data. Empty string translates to application/octet-stream
     * @return Task
     */
    public Task createNewBlobAsync(Connection c, String name, String mimeType) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "Async.pool.create_new_blob";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(name), Marshalling.toXMLRPC(mimeType)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
        return Types.toTask(result);
    }

    /**
     * Create a placeholder for a named binary blob of data that is associated with this pool
     *
     * @param name The name associated with the blob
     * @param mimeType The mime type for the data. Empty string translates to application/octet-stream
     * @return The reference of the blob, needed for populating its data
     */
    public Blob createNewBlob(Connection c, String name, String mimeType) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.create_new_blob";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(name), Marshalling.toXMLRPC(mimeType)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toBlob(result);
    }

    /**
     * Return a list of all the pools known to the system.
     *
     * @return references to all objects
     */
    public static Set<Pool> getAll(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_all";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toSetOfPool(result);
    }

    /**
     * Return a map of pool references to pool records for all pools known to the system.
     *
     * @return records of all objects
     */
    public static Map<Pool, Pool.Record> getAllRecords(Connection c) throws
       BadServerResponse,
       XenAPIException,
       XmlRpcException {
        String method_call = "pool.get_all_records";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        Object result = response.get("Value");
            return Types.toMapOfPoolPoolRecord(result);
    }

}