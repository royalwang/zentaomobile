package com.cnezsoft.zentao.data;

import android.database.Cursor;

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
     * Todo status enum
     */
    public enum Status {WAIT, DONE, DOING}

    /**
     * Get todo status
     * @return
     */
    public Status getStatus() {
        return Enum.valueOf(Status.class, getAsString(TodoColumn.status).trim().toUpperCase());
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
}
