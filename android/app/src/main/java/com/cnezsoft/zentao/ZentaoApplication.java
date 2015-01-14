package com.cnezsoft.zentao;

import android.app.Application;

/**
 * Zentao application
 * Created by Catouse on 2015/1/14.
 */
public class ZentaoApplication extends Application {
    private User user;
    private ZentaoConfig zentaoConfig;
    private UserPreferences userPreferences;

    public User getUser() {
        return user;
    }

    public ZentaoConfig getZentaoConfig() {
        return zentaoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        userPreferences = new UserPreferences(this);
        user = new User(userPreferences);
    }
}