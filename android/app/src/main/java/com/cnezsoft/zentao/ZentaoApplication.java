package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Todo;

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
    public static final String MESSAGE_IN_LOGIN = "com.cnezsoft.zentao.MESSAGE_IN_SYNC";
    public static final String MESSAGE_OUT_LOGIN_FINISH = "com.cnezsoft.zentao.MESSAGE_OUT_SYNC";
    public static final String MESSAGE_OUT_LOGIN_START = "com.cnezsoft.zentao.MESSAGE_IN_GET_ENTRY";

    private User user;
    private UserPreferences userPreferences;

    /**
     * User getter
     * @return
     */
    public User getUser() {
        if(user == null) {
            user = userPreferences.getUser();
        }
        return user;
    }

    public User getUser(String identify) {
        getUser();
        if(user == null || !user.getIdentify().equals(identify)) {
            user = userPreferences.getUser(identify);
        }
        return user;
    }

    public User swtichUser(String address, String account, String password) {
        user = getUser(User.createIdentify(address, account));
        user.setAddress(address)
            .setAccount(account)
            .setPassword(password);
        saveUser(user);
        return user;
    }

    public void saveUser(User user) {
        this.user = user;
        userPreferences.saveUser(user);
    }

    /**
     * Check user status and try login in background if his status is offline
     * @return
     */
    public boolean checkLogin() {
        User.Status status = user.getStatus();
        Log.v("APPLICATION", "Check Login Before: " + status);
        boolean result;
        if(status == User.Status.OFFLINE) {
            result = login();
        } else {
            result = status == User.Status.ONLINE;
        }
        Log.v("APPLICATION", "Check Login After: " + result);
        return result;
    }

    /**
     * Logout and change user
     * @param activity
     */
    public void logout(Activity activity) {
        saveUser(user.offline());
        openLoginActivity(activity);
    }

    /**
     * Login in background
     * @return
     */
    public boolean login(boolean async) {
        if(user.getStatus() != User.Status.UNKNOWN) {
            String identify = user.getIdentify();
            sendBroadcast(new Intent(MESSAGE_OUT_LOGIN_START)
                    .putExtra("identify", identify));
            OperateBundle<Boolean, User> loginResult = ZentaoAPI.tryLogin(user);
            boolean result = loginResult.getResult();

            if(result) {
                saveUser(loginResult.getValue().online());
            }
            sendBroadcast(new Intent(MESSAGE_OUT_LOGIN_FINISH)
                    .putExtra("result", result)
                    .putExtra("identify", identify));
            return result;
        } else {
            return false;
        }
    }

    /**
     * Try login in background first, then login in front if failed
     * @param fromActivity
     */
    public boolean login(Activity fromActivity) {
        User.Status status = getUser().getStatus();

        boolean result = (status == User.Status.ONLINE);
        if(status == User.Status.OFFLINE) {
            result = login();
        }
        if(!result) {
            openLoginActivity(fromActivity);
        }
        return result;
    }

    /**
     * The async task for login in Zentao server
     */
    private class LoginInBackgroundTask extends AsyncTask<Activity, Integer, Boolean> {
        private Activity activity;

        @Override
        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Activity... activities) {
            this.activity = activities[0];
        }

        protected void onPostExecute(boolean result) {

        }
    }

    /**
     * Open login activity
     * @param fromActivity
     */
    public void openLoginActivity(Activity fromActivity) {
        Intent intent = new Intent(fromActivity, LoginActivity.class);
        fromActivity.startActivityForResult(intent, LOGIN_REQUEST);
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
            case product:
                intent = new Intent(activity, ProductActivity.class);
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
     * Open activity
     * @param activity
     * @param nav
     */
    public void openActivity(Activity activity, AppNav nav) {
        openActivity(activity, nav, null);
    }

    /**
     * Open detail activity
     * @param activity
     * @param type
     * @param id
     */
    public void openDetailActivity(Activity activity, EntryType type, int id) {
        Intent intent;
        switch (type) {
            case Todo:
                intent = new Intent(activity, TodoDetailActivity.class);
                break;
            case Task:
                intent = new Intent(activity, TaskDetailActivity.class);
                break;
            case Bug:
                intent = new Intent(activity, BugDetailActivity.class);
                break;
            case Story:
                intent = new Intent(activity, StoryDetailActivity.class);
                break;
            case Project:
                intent = new Intent(activity, ProjectDetailActivity.class);
                break;
            case Product:
                intent = new Intent(activity, ProductDetailActivity.class);
                break;
            default:
                intent = new Intent(activity, EntryDetailActivity.class);
        }
        intent.putExtra(EntryDetailActivity.ARG_ENTRY_TYPE, type.name());
        intent.putExtra(EntryDetailActivity.ARG_ID, id);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userPreferences = new UserPreferences(this);
        user = getUser();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_IN_LOGIN);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        }, intentFilter);
    }

    /**
     * Get version name
     * @return
     */
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
}