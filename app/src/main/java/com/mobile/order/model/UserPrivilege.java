package com.mobile.order.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity
public class UserPrivilege implements Serializable {

    @Transient
    static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private boolean monitorActivation;
    private boolean monitorUsage;

    @Generated(hash = 1976086878)
    public UserPrivilege(Long id, boolean monitorActivation, boolean monitorUsage) {
        this.id = id;
        this.monitorActivation = monitorActivation;
        this.monitorUsage = monitorUsage;
    }

    @Generated(hash = 1558596137)
    public UserPrivilege() {
    }

    /**
     * Method used to prepare user privilege object
     *
     * @param jsonPrivilege Data which needs to be prepare json object
     * @return UserPrivilege
     * @throws JSONException Json exception
     */
    public static UserPrivilege fromJson(JSONObject jsonPrivilege) throws JSONException {
        UserPrivilege userPrivilege;
        userPrivilege = new UserPrivilege(
                jsonPrivilege.getLong("user_id"),
                jsonPrivilege.optBoolean("has_monitor_activation", false),
                jsonPrivilege.optBoolean("has_monitor_usage", false)
        );
        return userPrivilege;
    }

    public boolean hasMonitorAccess() {
        return (monitorActivation || monitorUsage);
    }

    public boolean getMonitorActivation() {
        return this.monitorActivation;
    }

    public void setMonitorActivation(boolean monitorActivation) {
        this.monitorActivation = monitorActivation;
    }

    public boolean getMonitorUsage() {
        return this.monitorUsage;
    }

    public void setMonitorUsage(boolean monitorUsage) {
        this.monitorUsage = monitorUsage;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
