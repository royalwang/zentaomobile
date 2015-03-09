package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.ZentaoApplication;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Task
 * Created by Catouse on 2015/1/30.
 */
public class Task extends DataEntry {

    /**
     * Task status
     */
    public enum Status implements IAccentIcon {
        _(MaterialColorSwatch.Grey, "question"),
        wait(MaterialColorSwatch.Brown, "clock-o"),
        doing(MaterialColorSwatch.Red, "play"),
        done(MaterialColorSwatch.Green, "check"),
        pause(MaterialColorSwatch.Orange, "pause"),
        cancel(MaterialColorSwatch.Grey, "ban"),
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
     * Task type
     */
    public enum Type {
        _,
        design,
        devel,
        test,
        study,
        discuss,
        ui,
        affair,
        misc
    }

    /**
     * Task closeReason
     */
    public enum CloseReason {
        _,
        done,
        cancel
    }

    /**
     * Task pagetabs
     */
    public enum PageTab implements IPageTab {
        assignedTo,
        openedBy,
        finishedBy;

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
            return EntryType.Task;
        }
    }

    /**
     * Get task type
     * @return
     */
    public Task.Type getTaskType() {
        try {
            return Enum.valueOf(Task.Type.class, getAsString(TaskColumn.type).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Type._;
        }
    }

    /**
     * Get close reason
     * @return
     */
    public CloseReason getCloseReason() {
        try {
            return Enum.valueOf(CloseReason.class, getAsString(TaskColumn.closeReason).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return CloseReason._;
        }
    }

    /**
     * Get task status
     * @return
     */
    public Task.Status getStatus() {
        try {
            return Enum.valueOf(Status.class, getAsString(TaskColumn.status).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Status._;
        }

    }

    /**
     * Constructor without params
     */
    public Task() {}

    /**
     * Constructor with JSONObject
     */
    public Task(JSONArray jsonArray, String[] keys) {
        super(jsonArray, keys);
    }

    /**
     * Constructor with json
     *
     * @param json
     */
    public Task(JSONObject json) {
        super(json);
    }

    /**
     * Constructor with cursor
     *
     * @param cursor
     */
    public Task(Cursor cursor) {
        super(cursor);
    }

    /**
     * The method for inherited classes to init settings
     */
    @Override
    protected void onCreate() {
        setType(EntryType.Task);
    }

    private float estimate;
    private float consumed;
    private float left;
    private float progress;

    public float getProgress() {
        return progress;
    }

    public float getEstimate() {
        return estimate;
    }

    public float getConsumed() {
        return consumed;
    }

    public float getLeft() {
        return left;
    }

    public void calculateHours() {
        estimate = getAsFloat(TaskColumn.estimate);
        consumed = getAsFloat(TaskColumn.consumed);
        left = getAsFloat(TaskColumn.left);

        float real = Math.max(estimate, consumed + left);
        progress = Math.min(1, real > 0 ? consumed / real : 0);
    }

    private Story story;


    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void setStory(Cursor cursor) {
        if(cursor!=null) {
            this.story = (Story) DataEntryFactory.create(EntryType.Story, cursor);
        }
    }

    private Product product;


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProduct(Cursor cursor) {
        if(cursor!=null) {
            this.product = (Product) DataEntryFactory.create(EntryType.Product, cursor);
        }
    }

    private Project project;


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setProject(Cursor cursor) {
        if(cursor!=null) {
            this.project = (Project) DataEntryFactory.create(EntryType.Project, cursor);
        }
    }
}
