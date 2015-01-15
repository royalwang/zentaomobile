package com.cnezsoft.zentao;

import android.app.Application;
import android.util.Log;

/**
 * Zentao application
 * Created by Catouse on 2015/1/14.
 */
public class ZentaoApplication extends Application {
    private User user;
    private ZentaoConfig zentaoConfig;
    private UserPreferences userPreferences;

    public ZentaoConfig getZentaoConfig() {
        return zentaoConfig;
    }

    public void setZentaoConfig(ZentaoConfig zentaoConfig) {
        this.zentaoConfig = zentaoConfig;
    }

    public User getUser() {
        Log.v("GET USER", user.toJSONString());
        return user;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        userPreferences = new UserPreferences(this);
        user = new User(userPreferences);
    }
}