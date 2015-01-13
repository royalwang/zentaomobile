package com.cnezsoft.zentao;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Catouse on 2015/1/12.
 */
public class ZentaoAPI
{
//    private Activity activity;
//
//    public ZentaoAPI(Activity activity) {
//        this.activity = activity;
//    }
//
//    public int networkStatus()
//    {
//        Context context = this.activity.getApplicationContext();
//        ConnectivityManager connectivityManager = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetInfo.getType();
//    }

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

    public static String concatUrl(Map<String, String> params, ZentaoConfig config, User user)
    {
        String moduleName = params.get("module");
        String methodName = params.get("method");
        String viewType = Helper.ifNullOrEmptyThen(params.get("viewType"), "json");
        String password;
        String url = user.getAddress();
        if(!url.endsWith("/")) url += "/";

        try {
            Log.v("concatUrl", config.toJSONString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                if(methodName.equals("mobilegetlist"))
                {
                    url += Helper.ifNullOrEmptyThen(params.get("type"), "full") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("object"), "all") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("range"), "0") + "-"
                        + Helper.ifNullThen(params.get("last"), "") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("records"), "1000") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("format"), "index") + "-"
                        + Helper.ifNullOrEmptyThen(params.get("zip"), "0");
                }
                else if(methodName.equals("mobilegetinfo"))
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
                if(methodName.equals("mobilegetlist"))
                {
                    url += "&type=" + Helper.ifNullOrEmptyThen(params.get("type"), "full")
                        + "&object=" + Helper.ifNullOrEmptyThen(params.get("object"), "all")
                        + "&range=" + Helper.ifNullOrEmptyThen(params.get("range"), "0")
                        + "&last=" + Helper.ifNullThen(params.get("last"), "")
                        + "&records=" + Helper.ifNullOrEmptyThen(params.get("records"), "all")
                        + "&format=" + Helper.ifNullOrEmptyThen(params.get("format"), "index")
                        + "&zip=" + Helper.ifNullOrEmptyThen(params.get("zip"), "0");
                }
                else if(methodName.equals("mobilegetinfo"))
                {
                    url += "&id=" + params.get("id")
                        + "&type=" + params.get("type");
                }
            }

            url += "&type=" + viewType + "&" + config.getSessionName() + "=" + config.getSessionID();
        }
        return url;
    }

    public static ZentaoConfig getConfig(String userAddress) throws MalformedURLException, JSONException {
        return new ZentaoConfig(Http.httpGetJSON(userAddress + "/index.php?mode=getconfig&test=1"));
    }

    public static OperateResult<Boolean> login(ZentaoConfig config, User user)
    {
        boolean result = false;
        String message = null;
        int code = 1;
        Map<String, String> params = new HashMap<String, String>(){{put("module", "user"); put("method", "login");}};
        try {
            JSONObject jsonResult = Http.httpGetJSON(concatUrl(params, config, user));

            String status = jsonResult.optString("status", "failed");
            result = status.equals("success");
            code = result ? 0 : 2;
            message = jsonResult.optString("reason", status);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            code = 4;
        }

        final int finalCode = code;
        return new OperateResult<Boolean>(result, message){{setCode(finalCode);}};
    }

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
}
