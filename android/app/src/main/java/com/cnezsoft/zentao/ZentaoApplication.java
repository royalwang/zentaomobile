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
    public static final String MESSAGE_IN_LOGIN = "com.cnezsoft.zentao.MESSAGE_IN_LOGIN";
    public static final String MESSAGE_OUT_LOGIN_FINISH = "com.cnezsoft.zentao.MESSAGE_OUT_LOGIN_FINISH";
    public static final String MESSAGE_OUT_LOGIN_START = "com.cnezsoft.zentao.MESSAGE_OUT_LOGIN_START";

    private User user;
    private UserPreferences userPreferences;

    /**
     * User getter
     * @return
     */
    public User getUser(boolean forceLoad) {
        if(forceLoad || user == null) {
            user = userPreferences.getUser();
        }
        return user;
    }

    public User getUser() {
        return getUser(false);
    }

    public User getUser(String identify) {
        getUser();
        if(user == null || !user.getIdentify().equals(identify)) {
            user = userPreferences.getUser(identify);
        }
        return user;
    }

    public User switchUser(String address, String account, String password) {
        Log.v("APPLICATION", "switch user before: " + user.toJSONString());
        user = getUser(User.createIdentify(address, account));
        user.setAddress(address)
            .setAccount(account)
            .setPassword(password)
            .setStatus(User.Status.OFFLINE);
        saveUser(user);
        Log.v("APPLICATION", "switch user after: " + user.toJSONString());
        return user;
    }

    public void saveUser(User user) {
        this.user = user;
        saveUser();
    }

    public void saveUser() {
        userPreferences.saveUser(user);
    }

    /**
     * Set OnUserAttrChangeListener to user preferences.
     * @param name
     * @param listener
     */
    public void setOnUserAttrChangeListener(String name, UserPreferences.OnUserAttrChangeListener listener) {
        userPreferences.setOnUserAttrChangeListener(name, listener);
    }

    /**
     * Set OnUserAttrChangeListener to user preferences.
     * @param names
     * @param listener
     */
    public void setOnUserAttrChangeListener(String[] names, UserPreferences.OnUserAttrChangeListener listener) {
        userPreferences.setOnUserAttrChangeListener(names, listener);
    }

    /**
     * Check user status and try login in background if his status is offline
     * @return
     */
    public boolean checkLogin() {
        User.Status status = getUser().getStatus();
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

    private OperateBundle<Boolean, User> tryLogin(User user) {
        OperateBundle<Boolean, User> result = ZentaoAPI.tryLogin(user);
        Log.v("APPLICATION", "Login result: " + result.getResult() + ", message: " + result.getMessage() + "(code: " + result.getCode() + ")");
        if(result.getResult()) {
            startService(new Intent(this, ZentaoSyncService.class));

            sendBroadcast(new Intent(Synchronizer.MESSAGE_IN_SYNC));
        }
        return result;
    }

    /**
     * Login in background
     * @return
     */
    public boolean login() {
        if(getUser().hasLoginCredentials()) {
            String identify = user.getIdentify();
            sendBroadcast(new Intent(MESSAGE_OUT_LOGIN_START)
                    .putExtra("identify", identify));
            OperateBundle<Boolean, User> loginResult = tryLogin(user);
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
     * Login in background with async task
     * @param onLoginFinished
     */
    public void login(final CustomAsyncTask.OnPostExecuteHandler<OperateBundle<Boolean, User>> onLoginFinished) {
        Log.v("APPLICATION", "Login in background async: " + getUser().toJSONString());
        if(getUser().hasLoginCredentials()) {
            final String identify = user.getIdentify();
            sendBroadcast(new Intent(MESSAGE_OUT_LOGIN_START)
                    .putExtra("identify", identify));

            new CustomAsyncTask<User, Integer, OperateBundle<Boolean, User>>(new CustomAsyncTask.DoInBackgroundHandler<User, OperateBundle<Boolean, User>>() {
                @Override
                public OperateBundle<Boolean, User> doInBackground(User... params) {
                    return tryLogin(user);
                }
            }, new CustomAsyncTask.OnPostExecuteHandler<OperateBundle<Boolean, User>>() {
                @Override
                public void onPostExecute(OperateBundle<Boolean, User> result) {
                    if(result.getResult()) {
                        saveUser(result.getValue().online());
                    }
                    sendBroadcast(new Intent(MESSAGE_OUT_LOGIN_FINISH)
                            .putExtra("result", result.getResult())
                            .putExtra("identify", identify));
                    onLoginFinished.onPostExecute(result);
                }
            }).execute(user);
        } else {
            Log.v("APPLICATION", "Login in background async: User information required.");
            onLoginFinished.onPostExecute(new OperateBundle<Boolean, User>(false) {{setCode(5);}});
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

    public void login(final Activity fromActivity, final CustomAsyncTask.OnPostExecuteHandler<Boolean> onLoginFinished) {
        User.Status status = getUser().getStatus();

        boolean result = (status == User.Status.ONLINE);
        if(status == User.Status.OFFLINE) {
            new CustomAsyncTask<User, Integer, Boolean>(new CustomAsyncTask.DoInBackgroundHandler<User, Boolean>() {
                @Override
                public Boolean doInBackground(User... params) {
                    return login();
                }
            }, new CustomAsyncTask.OnPostExecuteHandler<Boolean>() {
                @Override
                public void onPostExecute(Boolean loginResult) {
                    if(!loginResult) {
                        openLoginActivity(fromActivity);
                    }
                    if(onLoginFinished != null) {
                        onLoginFinished.onPostExecute(loginResult);
                    }
                }
            }).execute(user);
            return;
        }
        if(!result) {
            openLoginActivity(fromActivity);
        }
        if(onLoginFinished != null) {
            onLoginFinished.onPostExecute(result);
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