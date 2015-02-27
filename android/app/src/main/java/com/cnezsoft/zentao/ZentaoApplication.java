package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.cnezsoft.zentao.data.EntryType;

import org.apache.http.impl.client.DefaultHttpClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Zentao application
 * Created by Catouse on 2015/1/14.
 */
public class ZentaoApplication extends Application {

    public static final int LOGIN_REQUEST = 1;
    public static final String EXTRA_AUTO_LOGIN = "com.cnezsoft.zentao.extra.auto-login";
    private User user;
    private ZentaoConfig zentaoConfig;
    private UserPreferences userPreferences;
    private DefaultHttpClient defaultHttpClient;

    public DefaultHttpClient getDefaultHttpClient() {
        if(defaultHttpClient == null) {
            defaultHttpClient = new DefaultHttpClient();
        }
        return defaultHttpClient;
    }

    /**
     * ZentaoConfig getter
     * @return
     */
    public ZentaoConfig getZentaoConfig() {
        return zentaoConfig;
    }

    /**
     * ZentaoConfig setter
     * @param zentaoConfig
     */
    public void setZentaoConfig(ZentaoConfig zentaoConfig) {
        this.zentaoConfig = zentaoConfig;
    }

    /**
     * User getter
     * @return
     */
    public User getUser() {
        user.load();
        return user;
    }

    /**
     * Check user status
     * @param activity
     */
    public void checkUserStatus(Activity activity) {
        if(user.getStatus() != User.Status.Online)
        {
            login(activity, true);
        }
    }

    public boolean checkUserStatus() {
        User.Status userStatus = user.getStatus();
        Log.v("APPLICATION", "checkUserStatus: " + userStatus.toString());
        if(userStatus == User.Status.Unknown) {
            return false;
        } else if(userStatus == User.Status.Offline || zentaoConfig == null) {
            Log.v("APPLICATION", "The user is offline, now login again.");
            return login();
        }
        Log.v("APPLICATION", "checkUserStatus NOW: " + user.getStatus());
        return true;
    }

    /**
     * Logout and change user
     * @param activity
     */
    public void logout(Activity activity) {
        user.offline();
        user.save();
        login(activity, false);
    }

    /**
     * Login in background
     * @return
     */
    public boolean login() {
        if(user.getStatus() != User.Status.Unknown) {
            OperateBundle<Boolean, ZentaoConfig> loginResult = ZentaoAPI.tryLogin(user);
            boolean result = loginResult.getResult();

            if(result) {
                user.online();
                setZentaoConfig(loginResult.getValue());
            }
            return result;
        } else {
            return false;
        }
    }

    /**
     * Open login active and login
     * @param activity
     */
    public void login(Activity activity, boolean autoLogin) {
        Intent intent = new Intent(activity, LoginActivity.class);

        intent.putExtra(EXTRA_AUTO_LOGIN, autoLogin);
        activity.startActivityForResult(intent, LOGIN_REQUEST);
    }

    /**
     * Login in front
     * @param activity
     */
    public void login(Activity activity) {
        login(activity, false);
    }

    /**
     * Open activity by given app nav type
     * @param activity
     * @param nav
     * @param extras
     */
    public void openActivity(Activity activity, AppNav nav, Bundle extras) {
        Intent intent = null;
        switch (nav) {
            case home:
                intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case todo:
            case task:
            case bug:
            case story:
                intent = new Intent(activity, ListActivity.class);
                intent.putExtra(ListActivity.NAV_CURRENT, nav.toDashboardNav().ordinal());
                break;
            case project:
                intent = new Intent(activity, ProjectActivity.class);
                break;
            case setting:
                intent = new Intent(activity, SettingsActivity.class);
                break;
        }
        if(intent != null) {
            if(extras != null) {
                intent.putExtras(extras);
            }
            activity.startActivity(intent);
        }
    }

    /**
     * Open detail activity
     * @param activity
     * @param type
     * @param id
     */
    public void openDetailActivity(Activity activity, EntryType type, long id) {
        Intent intent = new Intent(activity, EntryDetailActivity.class);
        intent.putExtra(EntryDetailActivity.ARG_ENTRY_TYPE, type.name());
        intent.putExtra(EntryDetailActivity.ARG_ID, id);
        activity.startActivity(intent);
    }

    /**
     * Open activity
     * @param activity
     * @param nav
     */
    public void openActivity(Activity activity, AppNav nav) {
        openActivity(activity, nav, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        userPreferences = new UserPreferences(this);
        user = new User(userPreferences);
    }

    public String getVersionName()
    {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown version";
        }
    }

    /**
     * Open url with browser
     * @param url
     */
    public static void openBrowser(Activity activity, String url) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(viewIntent);
    }


    /**
     * @author Lonkly
     * @param variableName - name of drawable, e.g R.drawable.<b>image</b>
     * @param с - class of resource, e.g R.drawable.class or R.raw.class
     * @return integer id of resource
     */
    public static int getResId(String variableName, Class<?> с) {
        int resId = -1;
        try {
            Field field = с.getField(variableName);
            try {
                resId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;
    }

    /**
     * Get enum text string from resources
     * @param context
     * @param var
     * @return
     */
    public static String getEnumText(Context context, Enum var) {
        String name = var.name();
        Class<? extends Enum> cls = var.getClass();
        Class<?> dclCls = cls.getDeclaringClass();
        String resourceName = "enum_"
                + (dclCls != null ? (dclCls.getSimpleName() + "_") : "")
                + cls.getSimpleName() + "_" + var.name();

        int resId = getResId(resourceName, R.string.class);
        return resId >= 0 ? context.getString(resId) : name;
    }

    /**
     * Get enum text strings from resources as a array
     * @param context
     * @param enums
     * @return
     */
    public static String[] getEnumTexts(Context context, Enum[] enums) {
        List<String> list = new ArrayList<>(enums.length);
        for(Enum var: enums) {
            list.add(getEnumText(context, var));
        }
        return list.toArray(new String[list.size()]);
    }

//    public boolean isRunningInBackground() {
//        ActivityManager activityManager = (ActivityManager) this
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
//                .getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.processName.equals(this.getPackageName())) {
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//                    Log.i("APPLICATION", String.format("Background App:", appProcess.processName));
//                    return true;
//                }else{
//                    Log.i("APPLICATION", String.format("Foreground App:", appProcess.processName));
//                    return false;
//                }
//            }
//        }
//        return false;
//    }
}