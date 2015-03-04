package com.cnezsoft.zentao.data;

import android.database.Cursor;

import com.cnezsoft.zentao.Helper;
import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Product
 * Created by Catouse on 2015/2/26.
 */
public class Product extends DataEntry {

    public static MaterialColorSwatch accent(int id) {
        return MaterialColorSwatch.all[(id + MaterialColorSwatch.all.length/2)%MaterialColorSwatch.all.length];
    }

    /**
     * Product status
     */
    public enum Status implements IAccentIcon {
        _(MaterialColorSwatch.Grey, "question"),
        normal(MaterialColorSwatch.Green, "flag"),
        closed(MaterialColorSwatch.Grey, "dot-circle-o");

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
     * Product acl
     */
    public enum Acl {
        _,
        open,
        _private,
        custom
    }

    /**
     * Get product type
     * @return
     */
    public Status getStatus() {
        try {
            return Enum.valueOf(Status.class, getAsString(ProductColumn.status).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Status._;
        }
    }

    /**
     * Get product acl
     * @return
     */
    public Acl getAcl() {
        String typeName = getAsString(ProductColumn.acl).trim().toLowerCase();
        if(typeName.equals("private")) {
            typeName = "_" + typeName;
        } else if(Helper.isNullOrEmpty(typeName)) {
            return Acl._;
        }

        try {
            return Enum.valueOf(Acl.class, typeName);
        } catch (IllegalArgumentException e) {
            return Acl._;
        }
    }

    /**
     * Constructor without params
     */
    public Product() {}

    /**
     * Constructor with JSONObject
     */
    public Product(JSONArray jsonArray, String[] keys) {
        super(jsonArray, keys);
    }

    /**
     * Constructor with json
     *
     * @param json
     */
    public Product(JSONObject json) {
        super(json);
    }

    /**
     * Constructor with cursor
     *
     * @param cursor
     */
    public Product(Cursor cursor) {
        super(cursor);
    }

    /**
     * The method for inherited classes to init settings
     */
    @Override
    protected void onCreate() {
        setType(EntryType.Product);
    }

    private long storyCount, changedCount, draftCount, closeCount;

    public long getStoryCount() {
        return storyCount;
    }

    public long getChangedCount() {
        return changedCount;
    }

    public long getDraftCount() {
        return draftCount;
    }

    public long getCloseCount() {
        return closeCount;
    }

    public void setStoryCount(DAO dao) {
        String id = key();
        setStoryCount(dao.getStoryCountOfProduct(id, Story.Status.active),
                dao.getStoryCountOfProduct(id, Story.Status.changed),
                dao.getStoryCountOfProduct(id, Story.Status.draft),
                dao.getStoryCountOfProduct(id, Story.Status.closed));
    }

    public void setStoryCount(long storyCount, long changedCount, long draftCount, long closeCount) {
        this.storyCount = storyCount;
        this.changedCount = changedCount;
        this.draftCount = draftCount;
        this.closeCount = closeCount;
    }

    private long bugCount;

    public long getBugCount() {
        return bugCount;
    }

    public void setBugCount(long bugCount) {
        this.bugCount = bugCount;
    }
}
