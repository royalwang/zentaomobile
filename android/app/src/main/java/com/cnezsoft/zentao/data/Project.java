package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import com.cnezsoft.zentao.Helper;
import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.R;
import com.cnezsoft.zentao.ZentaoApplication;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

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
        try {
            return Enum.valueOf(Type.class, getAsString(ProjectColumn.type).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Type._;
        }
    }

    private Status status;

    /**
     * Get project type
     * @return
     */
    public Status getStatus() {
        if(status == null) {
            try {
                status = Enum.valueOf(Status.class, getAsString(ProjectColumn.status).trim().toLowerCase());
            } catch (IllegalArgumentException e) {
                status = Status._;
            }
        }
        return status;
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

        try {
            return Enum.valueOf(Acl.class, typeName);
        } catch (IllegalArgumentException e) {
            return Acl._;
        }
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

    public boolean isDoneLongTimeAgo() {
        if(getStatus() == Status.done) {
            Calendar now = Calendar.getInstance();
            Calendar closeDay = Calendar.getInstance();
            closeDay.add(Calendar.MONTH, -1);
            return closeDay.before(now);
        }
        return false;
    }

    public String getFriendlyTimeString(Context context) {
        Date date;
        String pattern;
        Status status = getStatus();
        String prefix = ZentaoApplication.getEnumText(context, status) + " ";
        switch (getStatus()) {
            case doing:
                date = getAsDate(ProjectColumn.end);
                pattern = context.getString(R.string.text_date_to_format);
                break;
            case suspended:
                date = getAsDate(ProjectColumn.end);
                pattern = context.getString(R.string.text_date_at_format);
                prefix += context.getString(R.string.text_date_end_at) + " ";
                break;
            case done:
                date = getAsDate(ProjectColumn.end);
                pattern = context.getString(R.string.text_date_at_format);
                break;
            default:
                date = getAsDate(ProjectColumn.begin);
                pattern = context.getString(R.string.text_date_at_format);
                break;
        }
        return  prefix + String.format(pattern, Helper.getFriendlyDateString(context, date));
    }

    public String getFriendlyDateSpanString(Context context) {
        return Helper.getFriendlyDateSpan(context, getAsDate(ProjectColumn.begin), getAsDate(ProjectColumn.end));
    }

    private float estimate; // 估计
    private float consumed; // 消耗
    private float left;     // 剩余

    public float getEstimate() {
        return estimate;
    }

    public float getConsumed() {
        return consumed;
    }

    public float getLeft() {
        return left;
    }

    public void calculateHours(Cursor tasksCursor) {
        estimate = 0;
        consumed = 0;
        left = 0;
        String statusCancelName = Task.Status.cancel.name();
        String closeReasonCancelName = Task.CloseReason.cancel.name();
        while (tasksCursor.moveToNext()) {
            estimate += tasksCursor.getFloat(tasksCursor.getColumnIndex(TaskColumn.estimate.name()));
            consumed += tasksCursor.getFloat(tasksCursor.getColumnIndex(TaskColumn.consumed.name()));
            if(!statusCancelName.equals(tasksCursor.getString(tasksCursor.getColumnIndex(TaskColumn.status.name())))
                    && !closeReasonCancelName.equals(tasksCursor.getString(tasksCursor.getColumnIndex(TaskColumn.closeReason.name())))) {
                left +=  tasksCursor.getFloat(tasksCursor.getColumnIndex(TaskColumn.left.name()));
            }
        }
    }

    public float getProgress() {
        float real = consumed + left;
        return Math.min(1, real > 0 ? (consumed / real) : 0);
    }

    public float getHour() {
        if(status == Status.done) {
            return consumed;
        } else {
            return -left;
        }
    }

    private long bugCount;

    public long getBugCount() {
        return bugCount;
    }

    public void setBugCount(long bugCount) {
        this.bugCount = bugCount;
    }

    public static MaterialColorSwatch accent(int id) {
        return MaterialColorSwatch.all[id%MaterialColorSwatch.all.length];
    }
}
