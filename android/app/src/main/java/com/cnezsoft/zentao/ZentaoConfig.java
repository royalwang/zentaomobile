package com.cnezsoft.zentao;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;

/**
 * Created by Catouse on 2015/1/12.
 * config as json like: {"version":"6.3","requestType":"PATH_INFO","pathType":"clean",
 * "requestFix":"-","moduleVar":"m","methodVar":"f","viewVar":"t","sessionVar":"sid",
 * "sessionName":"sid","sessionID":"joj7nhuq6mk0snot551oaju405","rand":4396,"expiredTime":"1440"}
 */
public class ZentaoConfig {
    private String version;
    private RequestType requestType;
    private String requestFix;
    private String moduleVar;
    private String methodVar;
    private String viewVar;
    private String sessionName;
    private String sessionID;
    private int rand;
    private String expiredTime;
    private boolean pro;
    private float versionNumber;
    private Date sessionTime;

    public ZentaoConfig(JSONObject jsonData) {
        this.version = jsonData.optString("version").toLowerCase();
        this.requestType = getRequestTypeFromName(jsonData.optString("requestType"));
        this.requestFix = jsonData.optString("requestFix");
        this.moduleVar = jsonData.optString("moduleVar");
        this.methodVar = jsonData.optString("methodVar");
        this.viewVar = jsonData.optString("viewVar");
        this.sessionName = jsonData.optString("sessionName");
        this.sessionID = jsonData.optString("sessionID");
        this.rand = jsonData.optInt("rand");
        this.expiredTime = jsonData.optString("expiredTime");

        // Analysis configurations
        this.sessionTime = new Date();
        this.pro = this.version.indexOf("pro") > -1;
        String[] verStr = this.version.replaceAll("[^0-9.]*([0-9.]*)[^0-9.]*", "$1").split("[.]", 2);
        this.versionNumber = Float.parseFloat((verStr.length > 0 ? verStr[0] : "0") + "." + (verStr.length > 1 ? verStr[1] : "0"));
    }

    public boolean isExpired()
    {
        return true;
    }

    public String toJSONString() throws JSONException {
        return new JSONStringer().object()
                .key("version").value(this.version)
                .key("pro").value(this.pro)
                .key("sessionTime").value(this.sessionTime)
                .key("versionNumber").value(this.versionNumber)
                .key("requestType").value(this.requestType)
                .key("requestFix").value(this.requestFix)
                .key("moduleVar").value(this.moduleVar)
                .key("methodVar").value(this.methodVar)
                .key("viewVar").value(this.viewVar)
                .key("sessionName").value(this.sessionName)
                .key("sessionID").value(this.sessionID)
                .key("rand").value(this.rand)
                .key("expiredTime").value(this.expiredTime)
                .endObject().toString();
    }

    public String getVersion() {
        return version;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getRequestFix() {
        return requestFix;
    }

    public String getModuleVar() {
        return moduleVar;
    }

    public String getMethodVar() {
        return methodVar;
    }

    public String getViewVar() {
        return viewVar;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getSessionName() {
        return sessionName;
    }

    public int getRand() {
        return rand;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public static RequestType getRequestTypeFromName(String name)
    {
        return (name == "PATH_INFO") ? RequestType.PATH_INFO : RequestType.GET;
    }
}
