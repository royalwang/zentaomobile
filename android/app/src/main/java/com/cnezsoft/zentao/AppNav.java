package com.cnezsoft.zentao;

import android.content.Context;

/**
 * Created by Catouse on 2015/1/28.
 */
public enum AppNav {
    home(0),
    todo(1),
    task(2),
    bug(3),
    story(4),
    setting(5);

    private final int position;

    public int getPosition() {
        return position;
    }

    public String text(Context context) {
        return context.getResources().getStringArray(R.array.drawer_list)[ordinal()];
    }

    public String text(String[] texts) {
        return texts[ordinal()];
    }

    AppNav(int position) {
        this.position = position;
    }

    public static AppNav fromPosition(int position) {
        for(AppNav nav: AppNav.values()) {
            if(nav.getPosition() == position)
                return nav;
        }
        return null;
    }

    public DashboardNav toDashboardNav() {
        switch (this) {
            case todo:
                return DashboardNav.todo;
            case task:
                return DashboardNav.task;
            case bug:
                return DashboardNav.bug;
            case story:
                return DashboardNav.story;
            default:
                return null;
        }
    }
}
