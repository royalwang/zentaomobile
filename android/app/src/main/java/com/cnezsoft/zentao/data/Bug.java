package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import com.cnezsoft.zentao.Helper;
import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.ZentaoApplication;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Bug
 * Created by Catouse on 2015/2/2.
 */
public class Bug extends DataEntry {

    /**
     * Bug type
     */
    public enum Type {
         _,
        codeerror,
        _interface,
        designchange,
        newfeature,
        designdefect,
        config,
        install,
        security,
        performance,
        standard,
        automation,
        trackthings,
        codeimprovement,
        others
    }

    /**
     * Bug os
     */
    public enum OS {
        _,
        all,
        windows,
        win8,
        win7,
        vista,
        winxp,
        win2012,
        win2008,
        win2003,
        win2000,
        android,
        ios,
        wp8,
        wp7,
        symbian,
        linux,
        freebsd,
        osx,
        unix,
        others
    }

    /**
     * Bug browser
     */
    public enum Browser {
        _,
        all,
        ie,
        ie11,
        ie10,
        ie9,
        ie8,
        ie7,
        ie6,
        chrome,
        firefox,
        firefox4,
        firefox3,
        firefox2,
        opera,
        oprea11,
        oprea10,
        opera9,
        safari,
        maxthon,
        uc,
        other
    }

    /**
     * Bug status
     */
    public enum Status implements IAccentIcon {
        _(MaterialColorSwatch.Grey, "question"),
        active(MaterialColorSwatch.Purple, "flag"),
        resolved(MaterialColorSwatch.Green, "check"),
        closed(MaterialColorSwatch.Grey, "dot-circle-o");

        Status(MaterialColorSwatch accentColor, String iconName) {
            this.accentColor = accentColor;
            this.iconName = iconName;
        }

        private MaterialColorSwatch accentColor;

        public MaterialColorSwatch accent() {
            return accentColor;
        }

        private String iconName;

        public String icon() {
            return iconName;
        }
    }

    /**
     * Bug resolution
     */
    public enum Resolution {
        _,
        bydesign,
        duplicate,
        external,
        fixed,
        notrepro,
        postponed,
        willnotfix,
        tostory
    }

    /**
     * Get bug type
     * @return
     */
    public Bug.Type getBugType() {
        String typeName = getAsString(BugColumn.type).trim().toLowerCase();
        if(typeName.equals("interface")) {
            typeName = "_" + typeName;
        } else if(Helper.isNullOrEmpty(typeName)) {
            return Type._;
        }

        try {
            return Enum.valueOf(Bug.Type.class, typeName);
        } catch (IllegalArgumentException e) {
            return Type._;
        }
    }

    /**
     * Get os
     * @return
     */
    public OS getOS() {
        try {
            return Enum.valueOf(Bug.OS.class, getAsString(BugColumn.os).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return OS._;
        }
    }

    /**
     * Get browser
     * @return
     */
    public Browser getBrowser() {
        try {
            return Enum.valueOf(Browser.class, getAsString(BugColumn.browser).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Browser._;
        }
    }

    /**
     * Get status
     * @return
     */
    public Bug.Status getStatus() {
        try {
            return Enum.valueOf(Status.class, getAsString(BugColumn.status).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Status._;
        }
    }

    /**
     * Get resolution
     * @return
     */
    public Resolution getResolution() {
        try {
            return Enum.valueOf(Resolution.class, getAsString(BugColumn.resolution).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Resolution._;
        }
    }

    /**
     * Get pagetab
     */
    public enum PageTab implements IPageTab {
        assignedTo,
        openedBy,
        resolvedBy;

        @Override
        public String text(Context context) {
            return ZentaoApplication.getEnumText(context, this);
        }

        @Override
        public PageTab[] tabs() {
            return values();
        }

        @Override
        public EntryType getEntryType() {
            return EntryType.Bug;
        }
    }

    /**
     * Get accent pri value
     * @return
     */
    @Override
    public int getAccentPri() {
        Integer pri = getValues().getAsInteger(BugColumn.severity.name());
        if(pri == null) return 0;
        else return pri;
    }

    /**
     * Constructor without params
     */
    public Bug() {}

    /**
     * Constructor with JSONObject
     */
    public Bug(JSONArray jsonArray, String[] keys) {
        super(jsonArray, keys);
    }

    /**
     * Constructor with json
     *
     * @param json
     */
    public Bug(JSONObject json) {
        super(json);
    }

    /**
     * Constructor with cursor
     *
     * @param cursor
     */
    public Bug(Cursor cursor) {
        super(cursor);
    }

    /**
     * The method for inherited classes to init settings
     */
    @Override
    protected void onCreate() {
        setType(EntryType.Bug);
    }
}
