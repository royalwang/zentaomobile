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

    public enum Status {WAIT, DONE, DOING}

    public Status getStatus() {
        return Enum.valueOf(Status.class, getAsString(TodoColumn.status).trim().toUpperCase());
    }

    public Todo() {

    }

    public Todo(JSONObject json) {
        super(json);
    }

    public Todo(Cursor cursor) {super(cursor);}

    @Override
    public void onCreate() {
        setType(EntryType.Todo);
    }

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
