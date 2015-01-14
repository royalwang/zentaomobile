package com.cnezsoft.zentao;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;

/**
 * The zentao configuration
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

    /**
     * Construct with an jsonData
     * @param jsonData
     */
    public ZentaoConfig(JSONObject jsonData) {
        this.version = jsonData.optString("version").toLowerCase();
        this.requestType = getRequestTypeFromName(jsonData.optString("requestType"));
        this.requestFix = jsonData.optString("requestFix");
        this.moduleVar = jsonData.optString("moduleVar");
        this.methodVar = jsonData.optString("methodVar");
        this.viewVar = jsonData.optString("viewVar");
        this.sessionName = jsonData.optString("sessionName", "sid");
        this.sessionID = jsonData.optString("sessionID");
        this.rand = jsonData.optInt("rand");
        this.expiredTime = jsonData.optString("expiredTime");

        // Analysis configurations
        this.sessionTime = new Date();
        this.pro = this.version.contains("pro");
        String[] verStr = this.version.replaceAll("[^0-9.]*([0-9.]*)[^0-9.]*", "$1").split("[.]", 2);
        this.versionNumber = Float.parseFloat((verStr.length > 0 ? verStr[0] : "0") + "." + (verStr.length > 1 ? verStr[1] : "0"));
    }

    /**
     * "isPro" getter
     * @return
     */
    public boolean isPro() {
        return pro;
    }

    /**
     * Version number getter
     * @return
     */
    public float getVersionNumber() {
        return versionNumber;
    }

    /**
     * Determine whether the session is expired
     * @return
     */
    public boolean isExpired()
    {
        return false;
    }

    /**
     * Return the config with json string
     * @return
     * @throws JSONException
     */
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

    /**
     * Version getter
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * "RequestType" gtter
     * @return
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * "RequestFix" getter
     * @return
     */
    public String getRequestFix() {
        return requestFix;
    }

    /**
     * "ModuleVar" getter
     * @return
     */
    public String getModuleVar() {
        return moduleVar;
    }

    /**
     * "MethodVar" getter
     * @return
     */
    public String getMethodVar() {
        return methodVar;
    }

    /**
     * "ViewVar" getter
     * @return
     */
    public String getViewVar() {
        return viewVar;
    }

    /**
     * "SessionID" getter
     * @return
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * "SessionName" getter
     * @return
     */
    public String getSessionName() {
        return sessionName;
    }

    /**
     * Rand getter
     * @return
     */
    public int getRand() {
        return rand;
    }

    /**
     * "ExpiredTime" getter
     * @return
     */
    public String getExpiredTime() {
        return expiredTime;
    }

    /**
     * Get RequestType from name
     * @param name
     * @return
     */
    public static RequestType getRequestTypeFromName(String name)
    {
        return (name.equals("PATH_INFO")) ? RequestType.PATH_INFO : RequestType.GET;
    }
}
