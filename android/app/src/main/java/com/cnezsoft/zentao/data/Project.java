package com.cnezsoft.zentao.data;

import android.database.Cursor;

import com.cnezsoft.zentao.Helper;
import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Project
 * Created by Catouse on 2015/2/26.
 */
public class Project extends DataEntry {

    /**
     * Project type
     */
    public enum Type {
        _,
        sprint,
        waterfall,
        ops
    }

    /**
     * Project status
     */
    public enum Status implements IAccentIcon {
        _(MaterialColorSwatch.Grey, "question"),
        wait(MaterialColorSwatch.Brown, "clock-o"),
        doing(MaterialColorSwatch.Red, "play"),
        suspended(MaterialColorSwatch.Orange, "pause"),
        done(MaterialColorSwatch.Green, "check");

        Status(MaterialColorSwatch accentColor, String iconName) {
            this.accentColor = accentColor;
            this.iconName = iconName;
        }

        private MaterialColorSwatch accentColor;

        private String iconName;

        @Override
        public String icon() {
            return iconName;
        }

        @Override
        public MaterialColorSwatch accent() {
            return accentColor;
        }
    }

    /**
     * Project acl
     */
    public enum Acl {
        _,
        open,
        _private,
        custom
    }

    /**
     * Get project type
     * @return
     */
    public Project.Type getProjectType() {
        return Enum.valueOf(Type.class, getAsString(ProjectColumn.type).trim().toLowerCase());
    }

    /**
     * Get project type
     * @return
     */
    public Status getStatus() {
        return Enum.valueOf(Status.class, getAsString(ProjectColumn.status).trim().toLowerCase());
    }

    /**
     * Get project acl
     * @return
     */
    public Acl getAcl() {
        String typeName = getAsString(ProjectColumn.acl).trim().toLowerCase();
        if(typeName.equals("private")) {
            typeName = "_" + typeName;
        } else if(Helper.isNullOrEmpty(typeName)) {
            return Acl._;
        }
        return Enum.valueOf(Acl.class, typeName);
    }

    /**
     * Constructor without params
     */
    public Project() {}

    /**
     * Constructor with JSONObject
     */
    public Project(JSONArray jsonArray, String[] keys) {
        super(jsonArray, keys);
    }

    /**
     * Constructor with json
     *
     * @param json
     */
    public Project(JSONObject json) {
        super(json);
    }

    /**
     * Constructor with cursor
     *
     * @param cursor
     */
    public Project(Cursor cursor) {
        super(cursor);
    }

    /**
     * The method for inherited classes to init settings
     */
    @Override
    protected void onCreate() {
        setType(EntryType.Project);
    }
}
