package com.cnezsoft.zentao.data;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Todo
 * Created by Catouse on 2015/1/15.
 */
public class Todo extends DataEntry {

    public enum Status {WAIT, DONE, DOING}

    public Status getStatus() {
        return Enum.valueOf(Status.class, getAsString("status").trim().toUpperCase());
    }

    public Todo() {
    }

    public Todo(JSONObject json) {
        super(json);
    }

    @Override
    public void createColumns(HashMap<String, String> columns) {
        super.createColumns(columns);
        columns.put("pri", TYPE_INT);
        columns.put("begin", TYPE_DATETIME);
        columns.put("end", TYPE_DATETIME);
        columns.put("type", TYPE_STRING);
        columns.put("name", TYPE_STRING);
        columns.put("status", TYPE_STRING);
    }

    @Override
    public void afterFromJSON(JSONObject json, HashSet<String> excepts) {
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

            put("begin", begin);
            put("end", end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
