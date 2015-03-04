package com.cnezsoft.zentao;

import com.cnezsoft.zentao.data.Todo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;
import java.util.HashMap;

/**
 * User
 * Created by sunhao on 15/3/4.
 */
public class User {
    static final String PASSWORD_WITH_MD5_FLAG = "%%%PWD_FLAG%%% ";

    public enum Status {
        UNKNOWN,
        OFFLINE,
        ONLINE;
    }

    private ZentaoConfig zentaoConfig;
    private Status status = Status.UNKNOWN;
    private HashMap<UserAttr, Object> values = new HashMap<>();

    public User() {}

    public User(Status status) {
        setStatus(status);
    }

    public User(String address, String account, String password) {
        setStatus(Status.OFFLINE);
        setAccount(account);
        setAddress(address);
        setPassword(password);
    }

    public ZentaoConfig getZentaoConfig() {
        return zentaoConfig;
    }

    public User setZentaoConfig(ZentaoConfig zentaoConfig) {
        this.zentaoConfig = zentaoConfig;
        return this;
    }

    public User setPassword(String password) {
        if(!password.startsWith(PASSWORD_WITH_MD5_FLAG)) {
            password = PASSWORD_WITH_MD5_FLAG + ZentaoAPI.md5(password);
        }
        put(UserAttr.passwordMd5, password);
        return this;
    }

    public HashMap<UserAttr, Object> getValues() {
        return values;
    }

    public void put(UserAttr attr, Object value) {
        values.put(attr, value);
    }

    public Object get(UserAttr attr) {
        Object value = values.get(attr);
        return value == null ? attr.getDefaultValue() : value;
    }

    public String getString(UserAttr attr) {
        Object value = get(attr);
        return value == null ? null : value.toString();
    }

    public String getPasswordMd5() {
        String password = getString(UserAttr.passwordMd5);
        if(password != null && password.startsWith(PASSWORD_WITH_MD5_FLAG)) {
            return password.substring(PASSWORD_WITH_MD5_FLAG.length());
        }
        return password;
    }

    public String getAccount() {
        return getString(UserAttr.account);
    }

    public User setAccount(String account) {
        put(UserAttr.account, account);
        return this;
    }

    public String getAddress() {
        return getString(UserAttr.address);
    }

    public User setAddress(String address) {
        put(UserAttr.address, address);
        return this;
    }

    public Date getLastSyncTime() {
        return (Date) get(UserAttr.lastSyncTime);
    }

    public User setLastSyncTime() {
        put(UserAttr.lastSyncTime, new Date());
        return this;
    }

    public Status getStatus() {
        // check user status
        if(zentaoConfig == null && status == Status.ONLINE) {
            status = Status.OFFLINE;
        }
        if(Helper.isNullOrEmpty(getAccount()) || Helper.isNullOrEmpty(getAddress())
                || Helper.isNullOrEmpty(getPasswordMd5())) {
            status = Status.UNKNOWN;
        }
        return status;
    }

    public User setStatus(Status status) {
        this.status = status;
        return this;
    }

    public User offline() {
        if(getStatus() == Status.ONLINE) {
            setStatus(Status.OFFLINE);
        }
        return this;
    }

    public User online() {
        setStatus(Status.ONLINE);
        return this;
    }

    public String getIdentify() {
        return createIdentify(getAddress(), getAccount());
    }

    public void fromJSON(JSONObject json) {
        put(UserAttr.email, json.optString("email", (String) UserAttr.email.getDefaultValue()));
        put(UserAttr.realName, json.optString("realname", (String) UserAttr.realName.getDefaultValue()));
        put(UserAttr.id, json.optString("id", (String) UserAttr.id.getDefaultValue()));
        put(UserAttr.role, json.optString("role", (String) UserAttr.role.getDefaultValue()));
        put(UserAttr.company, json.optString("company", (String) UserAttr.company.getDefaultValue()));
        put(UserAttr.gender, json.optString("gender", (String) UserAttr.gender.getDefaultValue()));
    }

    public String toJSONString() {
        try {
            JSONStringer stringer = new JSONStringer().object();
            for(UserAttr attr: UserAttr.values()) {
                stringer.key(attr.name());
                switch (attr.getType()) {
                    case INT:
                    case LONG:
                        stringer.value((long) get(attr));
                        break;
                    case DOUBLE:
                        stringer.value((double) get(attr));
                        break;
                    case BOOLEAN:
                        stringer.value((boolean) get(attr));
                        break;
                    default:
                        stringer.value(get(attr));
                }
            }
            return stringer.endObject().toString();
        } catch (JSONException e) {
            return "[EMPTY USER]";
        }
    }

    public static String createIdentify(String address, String account) {
        return account + "@" + address;
    }

    public static String getAccountFromIdentify(String identify) {
        return identify != null ? identify.substring(0, identify.indexOf("@")) : null;
    }

    public static String getAddressFromIdentify(String identify) {
        return identify != null ? identify.substring(identify.indexOf("@") + 1) : null;
    }
}
