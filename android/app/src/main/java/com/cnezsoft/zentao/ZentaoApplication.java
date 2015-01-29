package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cnezsoft.zentao.data.EntryType;

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

    public void openActivity(Activity activity, AppNav nav, Bundle extras) {
        Intent intent = null;
        switch (nav) {
            case home:
                intent = new Intent(activity, MainActivity.class);
                break;
            case todo:
            case task:
            case bug:
            case story:
                intent = new Intent(activity, ListActivity.class);
                intent.putExtra(ListActivity.NAV_CURRENT, nav.toDashboardNav().ordinal());
                break;
        }
        if(intent != null) {
            if(extras != null) {
                intent.putExtras(extras);
            }
            activity.startActivity(intent);
        }
    }

    public void openDetailActivity(Activity activity, EntryType type, long id) {
        Intent intent = new Intent(activity, EntryDetailActivity.class);
        intent.putExtra(EntryDetailActivity.ARG_ENTRY_TYPE, type.name());
        intent.putExtra(EntryDetailActivity.ARG_ID, id);
        activity.startActivity(intent);
    }

    public void openActivity(Activity activity, AppNav nav) {
        openActivity(activity, nav, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        userPreferences = new UserPreferences(this);
        user = new User(userPreferences);
    }

    /**
     * Open url with browser
     * @param url
     */
    public static void openBrowser(Activity activity, String url) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(viewIntent);
    }
}