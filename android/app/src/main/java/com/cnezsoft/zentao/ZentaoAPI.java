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
                        + Helper.ifNullOrEmptyThen(params.get("format"), "index") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("zip"), "0");
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
                        + "&format=" + Helper.ifNullOrEmptyThen(params.get("format"), "index")
                        + "&zip=" + Helper.ifNullOrEmptyThen(params.get("zip"), "0");
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
    public static ZentaoConfig getConfig(String userAddress) throws MalformedURLException, JSONException {
        return new ZentaoConfig(Http.getJSON(userAddress + "/index.php?mode=getconfig"));
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
        try {
            JSONObject jsonResult = Http.getJSON(concatUrl(params, config, user));

            String status = jsonResult.optString("status", "failed");
            result = status.equals("success");
            code = result ? 0 : 2;
            message = jsonResult.optString("reason");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            code = 4;
        }

        OperateResult<Boolean> operateResult = new OperateResult<>(result, message);
        operateResult.setCode(code);
        return operateResult;
    }

    /**
     * Get zentao config then login in zentao
     * The method should called in async task
     *
     * @param user
     * @return
     */
    public static OperateBundle<Boolean, ZentaoConfig> tryLogin(User user)
    {
        try {
            ZentaoConfig config = getConfig(user.getAddress());

            // Check zentao version
            if(!config.isPro() || config.getVersionNumber() < 4.3f )
            {
                return new OperateBundle<Boolean, ZentaoConfig>(false, config){{setCode(3);}};
            }

            OperateResult<Boolean> result = login(config, user);

            return result.toOperateBundle(config);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new OperateBundle<Boolean, ZentaoConfig>(false){{setCode(1);}};
        } catch (JSONException e) {
            e.printStackTrace();
            return new OperateBundle<Boolean, ZentaoConfig>(false){{setCode(4);}};
        }
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @param type
     * @param entryType
     * @param range
     * @param last
     * @param records
     * @param format
     * @param zip
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config, User user, String type, EntryType entryType,
        int range, Date last, int records, String format, boolean zip) {
        JSONObject json;
        boolean result;
        String message;
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("module", "api");
        parmas.put("method", "mobileGetList");
        parmas.put("type", type);
        parmas.put("object", entryType == EntryType.Default ? "all" : entryType.name().toLowerCase());
        parmas.put("range", range + "");
        parmas.put("last", ((int) Math.floor(last.getTime()/1000)) + "");
        parmas.put("records", records + "");
        parmas.put("zip", zip ? "1" : "0");
        parmas.put("format", format);
        try {
            json = Http.getJSON(concatUrl(parmas, config, user));
            String status = json.optString("status", "failed");
            result = status.equals("success");
            message = json.optString("reason");

            if(result) {
                json = new JSONObject(json.optString("data"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new OperateBundle<>(false);
        } catch (JSONException e) {
            e.printStackTrace();
            return new OperateBundle<>(false);
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
     * @param last
     * @param records
     * @param format
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config, User user,
            String type, EntryType entryType, int range, Date last, int records, String format) {
        return getDataList(config, user, type, entryType, range, last, records, format, false);
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @param entryType
     * @param last
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config,
            User user, EntryType entryType, Date last) {
        return getDataList(config, user, "increment", entryType, 0, last, 500, "index", false);
    }

    /**
     * Get data list
     * @param config
     * @param user
     * @param last
     * @return
     */
    public static OperateBundle<Boolean, JSONObject> getDataList(ZentaoConfig config,
            User user, Date last) {
        return getDataList(config, user, "increment", EntryType.Default, 0, last, 1000, "index", false);
    }
}
