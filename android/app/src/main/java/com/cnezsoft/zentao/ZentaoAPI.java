package com.cnezsoft.zentao;

import com.cnezsoft.zentao.data.EntryType;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Zentao API helpers
 * Created by Catouse on 2015/1/12.
 */
public class ZentaoAPI
{
    public enum RequestType {
        GET,
        PATH_INFO
    }

    public static final boolean GZIP_REQUEST = true;

    /**
     * Encrypted string with 'md5' algorithm
     * @param s
     * @return
     */
    public static String md5(String s)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(), 0, s.length());
            String hash = new BigInteger(1, digest.digest()).toString(16);
            return hash;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Concat zentao api url with params
     * @param params
     * @param config
     * @param user
     * @return
     */
    public static String concatUrl(Map<String, String> params, ZentaoConfig config, User user)
    {
        String moduleName = params.get("module");
        String methodName = params.get("method");
        String viewType = Helper.ifNullOrEmptyThen(params.get("viewType"), "json");
        String password;
        String url = user.getAddress();
        if(!url.endsWith("/")) url += "/";

        if(config.getRequestType() == RequestType.PATH_INFO)
        {
            if(moduleName.equals("user") && methodName.equals("login"))
            {
                password = md5(user.getPasswordMD5() + config.getRand());
                url += "user-login." + viewType
                    + "?account=" + user.getAccount()
                    + "&password=" + password
                    + "&" + config.getSessionName() + "=" + config.getSessionID();
                return url;
            }

            url += moduleName + "-" + methodName + "-";

            if(moduleName.equals("api"))
            {
                if(methodName.toLowerCase().equals("mobilegetlist"))
                {
                    url += Helper.ifNullOrEmptyThen(params.get("type"), "full") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("object"), "all") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("range"), "0") + "-"
                        + Helper.ifNullThen(params.get("last"), "") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("records"), "1000") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("format"), "index");
                }
                else if(methodName.toLowerCase().equals("mobilegetinfo"))
                {
                    url += params.get("id") + "-" + params.get("type");
                }
            }

            if(url.endsWith("-"))
            {
                url = url.substring(0, url.length() - 1);
            }

            url += "." + viewType + "?" + config.getSessionName() + "=" + config.getSessionID();
        }
        else // config.getRequestType() == RequestType.GET
        {
            url += "/index.php?";
            if(moduleName.equals("user") && methodName.equals("login"))
            {
                password = md5(user.getPasswordMD5() + config.getRand());
                url += "m=user&f=login&account=" + user.getAccount() +
                        "&password=" + password + "&" + config.getSessionName() +
                        "=" + config.getSessionID() + "&t=" + viewType;
                return url;
            }

            url += "m=" + moduleName + "&f=" + methodName;

            if(moduleName.equals("api"))
            {
                if(methodName.toLowerCase().equals("mobilegetlist"))
                {
                    url += "&type=" + Helper.ifNullOrEmptyThen(params.get("type"), "full")
                        + "&object=" + Helper.ifNullOrEmptyThen(params.get("object"), "all")
                        + "&range=" + Helper.ifNullOrEmptyThen(params.get("range"), "0")
                        + "&last=" + Helper.ifNullThen(params.get("last"), "")
                        + "&records=" + Helper.ifNullOrEmptyThen(params.get("records"), "all")
                        + "&format=" + Helper.ifNullOrEmptyThen(params.get("format"), "index");
                }
                else if(methodName.toLowerCase().equals("mobilegetinfo"))
                {
                    url += "&id=" + params.get("id")
                        + "&type=" + params.get("type");
                }
            }

            url += "&type=" + viewType + "&" + config.getSessionName() + "=" + config.getSessionID();
        }
        return url;
    }

    /**
     * Get zentao config
     * The method should called in async task
     *
     * @param userAddress
     * @return
     * @throws MalformedURLException
     * @throws JSONException
     */
    public static ZentaoConfig getConfig(String userAddress) {
        JSONObject jsonConfig = Http.getJSON(userAddress + "/index.php?mode=getconfig");
        if(jsonConfig == null) return null;
        return new ZentaoConfig(jsonConfig);
    }

    /**
     * Login in zentao
     * The method should called in async task
     *
     * @param config
     * @param user
     * @return
     */
    public static OperateResult<Boolean> login(ZentaoConfig config, User user)
    {
        boolean result = false;
        String message = null;
        int code = 1;
        Map<String, String> params = new HashMap<String, String>(){{put("module", "user"); put("method", "login");}};
        JSONObject jsonResult = Http.getJSON(concatUrl(params, config, user));

        if(jsonResult != null) {
            String status = jsonResult.optString("status", "failed");
            result = status.equals("success");
            code = result ? 0 : 2;
            message = jsonResult.optString("reason");

            if(result) {
                try {
                    JSONObject jsonUser = jsonResult.getJSONObject("user");
                    user.setEmail(jsonUser.optString("email"), false)
                        .setRealname(jsonUser.optString("realname"), false)
                        .setId(jsonUser.optString("realname"), false)
                        .setRole(jsonUser.optString("role"), false)
                        .setGender(jsonUser.optString("gender"), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            OperateResult<Boolean> operateResult = new OperateResult<>(result, message);
            operateResult.setCode(code);
            return operateResult;
        } else {
            return new OperateResult<Boolean>(false, "Can't get data from remote server.") {{setCode(4);}};
        }
    }

    /**
     * Get zentao config then login in zentao
     * The method should called in async task
     *
     * @param user
     * @return
     */
    public static OperateBundle<Boolean, ZentaoConfig> tryLogin(User user) {
        if(user.checkUserStatus() == User.Status.Unknown) {
            return new OperateBundle<Boolean, ZentaoConfig>(false){{setCode(5);}};
        }

        ZentaoConfig config = getConfig(user.getAddress());

        // Check zentao version
        if(config == null) {
            // Can't connect to the zentao server.
            return new OperateBundle<Boolean, ZentaoConfig>(false){{setCode(1);}};
        }
        else if(!config.isPro() || config.getVersionNumber() < 4.3f ) {
            // Zentao version is not correct
            return new OperateBundle<Boolean, ZentaoConfig>(false, config){{setCode(3);}};
        }

        OperateResult<Boolean> result = login(config, user);

        return result.toOperateBundle(config);
    }

    public static OperateBundle<Boolean, JSONObject> getDataItem(ZentaoConfig config, User user, EntryType entryType, String id) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("module", "api");
        parmas.put("method", "mobileGetInfo");
        parmas.put("type", entryType.name().toLowerCase());
        parmas.put("id", id);

        JSONObject json = Http.getJSON(concatUrl(parmas, config, user), GZIP_REQUEST);
        boolean result;
        String message;
        if(json != null) {
            String status = json.optString("status", "failed");
            result = status.equals("success");
            message = json.optString("reason");

            if(result) {
                json = json.optJSONObject("data");
                if(json == null && Helper.isNullOrEmpty(message)) {
                    message = "No new data. ";
                }
            }
        } else {
            result = false;
            message = "Cant' get data from remote server.";
        }
        return new OperateBundle<>(result, message, json);
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @param type
     * @param entryType
     * @param range
     * @param records
     * @param format
     * @param zip
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config, User user, String type, EntryType entryType,
        int range, int records, String format, boolean zip) {
        JSONObject json;
        boolean result;
        String message;
        Map<String, String> parmas = new HashMap<>();
        Date lastSyncTime = user.getLastSyncTime();
        parmas.put("module", "api");
        parmas.put("method", "mobileGetList");
        parmas.put("type", type);
        parmas.put("object", entryType == EntryType.Default ? "all" : entryType.name().toLowerCase());
        parmas.put("range", range + "");
        parmas.put("last", ((int) Math.floor(lastSyncTime.getTime()/1000)) + "");
        parmas.put("records", records + "");
        parmas.put("zip", zip ? "1" : "0");
        parmas.put("format", format);

        json = Http.getJSON(concatUrl(parmas, config, user), GZIP_REQUEST);
        if(json != null) {
            String status = json.optString("status", "failed");
            result = status.equals("success");
            message = json.optString("reason");

            if(result) {
                json = json.optJSONObject("data");
                if(json == null && Helper.isNullOrEmpty(message)) {
                    message = "No new data. last sync time=" + lastSyncTime.toString();
                } else {
                    message = "last sync time=" + lastSyncTime.toString();
                }
            }
        }
        else {
            result = false;
            message = "Cant' get data from remote server.";
        }
        return new OperateBundle<>(result, message, json);
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @param type
     * @param entryType
     * @param range
     * @param records
     * @param format
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config, User user,
            String type, EntryType entryType, int range, int records, String format) {
        return getDataList(config, user, type, entryType, range, records, format, false);
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @param entryType
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config,
            User user, EntryType entryType) {
        return getDataList(config, user, "increment", entryType, 0, 1000, "index", false);
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config,
            User user) {
        return getDataList(config, user, "increment", EntryType.Default, 0, 1000, "index", false);
    }
}
