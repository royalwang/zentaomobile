package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import com.cnezsoft.zentao.ZentaoApplication;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Catouse on 2015/1/30.
 */
public class Task extends DataEntry {

    public enum Status {
        _,
        wait,
        doing,
        done,
        pause,
        cancel,
        closed
    }

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

    public enum CloseReason {
        _,
        done,
        cancel
    }

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
        return Enum.valueOf(Task.Type.class, getAsString(TaskColumn.type).trim().toLowerCase());
    }

    /**
     * Get close reason
     * @return
     */
    public CloseReason getCloseReason() {
        return Enum.valueOf(CloseReason.class, getAsString(TaskColumn.closeReason).trim().toLowerCase());
    }

    /**
     * Get task status
     * @return
     */
    public Task.Status getStatus() {
        return Enum.valueOf(Status.class, getAsString(TaskColumn.status).trim().toLowerCase());
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
}
