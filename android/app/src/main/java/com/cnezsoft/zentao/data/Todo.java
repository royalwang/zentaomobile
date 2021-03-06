package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import com.cnezsoft.zentao.Helper;
import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.ZentaoApplication;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Todo
 * Created by Catouse on 2015/1/15.
 */
public class Todo extends DataEntry {
    /**
     * Todo types
     */
    public enum Types implements IAccentIcon {
        custom(MaterialColorSwatch.Blue, "tag"),
        bug(MaterialColorSwatch.Pink, "bug"),
        task(MaterialColorSwatch.Green, "tasks");

        private MaterialColorSwatch accentColor;
        private String iconName;

        Types(MaterialColorSwatch accentColor, String iconName) {
            this.accentColor = accentColor;
            this.iconName = iconName;
        }

        public String icon() {
            return iconName;
        }

        public MaterialColorSwatch accent() {
            return accentColor;
        }
    }

    /**
     * Page tabs
     */
    public enum PageTab implements IPageTab {
        undone,
        done;

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
            return EntryType.Todo;
        }
    }

    /**
     * Group
     */
    public enum Group implements IListFilter {
        none,
        time,
        pri,
        type;

        @Override
        public String text(Context context) {
            return ZentaoApplication.getEnumText(context, this);
        }

        @Override
        public Group[] all() {
            return values();
        }
    }

    /**
     * Order
     */
    public enum Order implements IListFilter {
        begin,
        pri,
        _id;

        @Override
        public String text(Context context) {
            return ZentaoApplication.getEnumText(context, this);
        }

        @Override
        public Order[] all() {
            return values();
        }
    }

    /**
     * Todo status enum
     */
    public enum Status implements IAccentIcon {
        wait(MaterialColorSwatch.Grey, "clock-o"),
        done(MaterialColorSwatch.Green, "check"),
        doing(MaterialColorSwatch.Pink, "play");

        private MaterialColorSwatch accentColor;
        private String iconName;

        Status(MaterialColorSwatch accentColor, String iconName) {
            this.accentColor = accentColor;
            this.iconName = iconName;
        }

        public String icon() {
            return iconName;
        }

        public MaterialColorSwatch accent() {
            return accentColor;
        }
    }

    /**
     * Get todo status
     * @return
     */
    public Status getStatus() {
        String statusStr = getAsString(TodoColumn.status);
        if(statusStr != null)
            try {
                return Enum.valueOf(Status.class, statusStr.trim().toLowerCase());
            } catch (IllegalArgumentException e) {
                return Status.wait;
            }
        else
            return Status.wait;
    }

    /**
     * Get todo type
     * @return
     */
    public Types getTodoType() {
        String typeStr = getAsString(TodoColumn.type);
        if(typeStr != null) {
            try {
                return Enum.valueOf(Types.class, typeStr.trim().toLowerCase());
            } catch (IllegalArgumentException e) {
                return Types.custom;
            }
        }
        return Types.custom;
    }

    /**
     * Constructor without params
     */
    public Todo() {
    }

    /**
     * Constructor with JSONObject
     * @param json
     */
    public Todo(JSONObject json) {
        super(json);
    }

    /**
     * Constructor with JSONObject
     */
    public Todo(JSONArray jsonArray, String[] keys) {
        super(jsonArray, keys);
    }

    /**
     * Constructor with Cursor
     * @param cursor
     */
    public Todo(Cursor cursor) {super(cursor);}

    /**
     * Set entry type
     */
    @Override
    public void onCreate() {
        setType(EntryType.Todo);
    }

    /**
     * Handle excepts attributes after init values form JSONObject
     * @param json
     * @param excepts
     */
    @Override
    public void afterFromJSON(JSONObject json, HashSet<IColumn> excepts) {
        try {
            String date = json.optString("date", null);
            DateFormat formatter = DateFormat.getDateTimeInstance();
            Date begin, end;
            if(date != null)
            {
                begin = formatter.parse(date + " " + json.optString("begin", "00:00:00"));
                end = formatter.parse(date + " " + json.optString("end", "23:59:59"));

                if(end.before(begin))
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(end);
                    cal.add(Calendar.DATE, 1);
                    end = cal.getTime();
                }
            }
            else
            {
                begin = formatter.parse(json.optString("begin", "00:00:00"));
                end = formatter.parse(json.optString("end", "23:59:59"));
            }

            put(TodoColumn.begin, begin);
            put(TodoColumn.end, end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getFriendlyTimeString(Context context) {
        return Helper.getFriendlyDateTimeSpan(context, getAsDate(TodoColumn.begin), getAsDate(TodoColumn.end));
    }

    public boolean isExpired() {
        return getAsDate(TodoColumn.end).before(new Date());
    }
}
