package com.cnezsoft.zentao;

import android.content.Context;

/**
 * App navs
 * Created by Catouse on 2015/1/28.
 */
public enum AppNav {
    home("home"),
    todo("check-square-o"),
    task("tasks"),
    bug("bug"),
    story("lightbulb-o"),
//    project("folder"),
//    product("cubes"),
    setting("cog");

    private final String icon;

    public String getIcon() {
        return icon;
    }

    /**
     * Get position
     * @return
     */
    public int getPosition() {
        return ordinal();
    }

    /**
     * Get text
     * @param context
     * @return
     */
    public String text(Context context) {
        return ZentaoApplication.getEnumText(context, this);
    }

    AppNav(String icon) {
        this.icon = icon;
    }

    /**
     * Get nav from position
     * @param position
     * @return
     */
    public static AppNav fromPosition(int position) {
        for(AppNav nav: AppNav.values()) {
            if(nav.getPosition() == position)
                return nav;
        }
        return null;
    }

    /**
     * Convert nav to dashboard nav
     * @return
     */
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
//            case product:
//                return DashboardNav.product;
//            case project:
//                return DashboardNav.project;
            default:
                return null;
        }
    }
}
