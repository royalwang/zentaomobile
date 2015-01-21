package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

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

    /**
     * Zentaoconfig getter
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
        Log.v("GET USER", user.toJSONString());
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
//        activity.startActivity(intent);

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

    @Override
    public void onCreate() {
        super.onCreate();

        userPreferences = new UserPreferences(this);
        user = new User(userPreferences);
    }
}