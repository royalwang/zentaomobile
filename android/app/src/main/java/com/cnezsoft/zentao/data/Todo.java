package com.cnezsoft.zentao.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Todo
 * Created by Catouse on 2015/1/15.
 */
public class Todo {

    public enum Status {WAIT, DONE, DOING}

    public int pri;
    public Date begin;
    public Date end;
    public String type;
    public String name;
    public Status status;

    public Todo() {
    }

    public Todo(JSONObject json) throws ParseException {
        id = json.optInt("id");
        type = json.optString("type");
        pri = json.optInt("pri");
        name = json.optString("name");
        status = Enum.valueOf(Status.class, json.optString("status").trim().toUpperCase());

        String date = json.optString("date", null);
        DateFormat formatter = DateFormat.getDateTimeInstance();
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
    }

    public String toJSONString() {
        try {
            return new JSONStringer().object()
                    .key("id").value(this.id)
                    .key("begin").value(this.begin)
                    .key("end").value(this.end)
                    .key("type").value(this.type)
                    .key("pri").value(this.pri)
                    .key("name").value(this.name)
                    .key("status").value(this.status.name())
                    .endObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "[Empty todo]";
        }
    }
}
