package com.cnezsoft.zentao;

import android.content.Context;
import android.util.Log;

import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Todo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Synchronizer
 *
 * Created by Catouse on 2015/1/19.
 */
public class Synchronizer {
    private Context context;
    private User user;
    private ZentaoConfig zentaoConfig;

    private Timer timer;//计时器声明
    private TimerTask timerTask;//计时器Task声明
    private int counter = 0;
    private final int maxLoginInterval = 1000*60*5;
    private long lastLoginTime = 0;
    private Date lastSyncTime = new Date(0);

    public Synchronizer(Context context) {
        this.context = context;
        ZentaoApplication application = (ZentaoApplication) context.getApplicationContext();
        user = application.getUser();
        zentaoConfig = application.getZentaoConfig();
    }

    public boolean sync() {
        Log.v("SYNC", "============================");
        User.Status userStatus = user.getStatus();
        if(userStatus == User.Status.Offline) {
            Long thisLoginTime = new Date().getTime();
            if((thisLoginTime - lastLoginTime) < maxLoginInterval) {
                Log.w("SYNC", "虽然需要重新登录，但为了避免频繁登录，取消了尝试");
                return false;
            } else {
                lastLoginTime = thisLoginTime;
            }
            if(!ZentaoAPI.tryLogin(user).getResult()) {
                return false;
            }
        }
        else if(userStatus == User.Status.Unknown) {
            return false;
        }

        Date thisSyncTime = new Date();
        Log.v("SYNC", "lastSyncTime:" + lastSyncTime + " -> " + lastSyncTime.getTime());
        Log.v("SYNC", "thisSyncTime:" + thisSyncTime + " -> " + thisSyncTime.getTime());

        OperateBundle<Boolean, JSONObject> result = ZentaoAPI.getDataList(zentaoConfig, user, lastSyncTime);
        if(result.getResult()) {
            JSONObject data = result.getValue();
            Log.v("SYNC", "success: " + result.getMessage() + ", data: " + (data != null ? data.toString() : "no data."));

            if(data != null) {
                DAO dao = new DAO(context);
                OperateResult<Boolean> daoResult = dao.save(getEntriesFromJSON(data));
                dao.close();
                Log.v("SYNC", "dao result: " + daoResult.toString());
            }

            lastSyncTime = thisSyncTime;
            return true;
        }
        Log.v("SYNC", "result: " + "false");

        return false;
    }

    public ArrayList<DataEntry> getEntriesFromJSON(JSONObject jsonData) {
        Log.v("SYNC", "getEntriesFromJSON: " + jsonData.toString());
        ArrayList<DataEntry> entries = new ArrayList<>();
        String name;
        EntryType entryType;
        DataEntry entry;
        JSONObject data;
        JSONArray set;
        String[] keys;
        int setLength;
        Iterator<String> names = jsonData.keys();
        while (names.hasNext()) {
            name = names.next();
            entryType = EntryType.fromName(name);
            if(entryType != null) {
                Log.v("SYNC", "handle json data: " + entryType.toString());
                try {
                    data = jsonData.getJSONObject(name);
                    set = data.getJSONArray("set");
                    setLength = set.length();
                    Log.v("SYNC", "data set length: " + setLength);
                    if(setLength <= 0) continue;

                    keys = Helper.getStringArrayFromJSON(data.getJSONArray("key"));

                    switch (entryType) {
                        case Todo:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Todo(set.getJSONArray(i), keys);
                                entries.add(entry);
                            }
                            break;
                    }

                } catch (JSONException e) {
                    Log.w("SYNC", "'" + name + "' JSON data's format is not correct.");
                    e.printStackTrace();
                }
            }
        }

        // todo items
        Log.v("SYNC", entries.size() + " data to save in the database.");
//        for(DataEntry entry1: entries) {
//            Log.v("SYNC", entry1.getType().name() + ": " + entry1.toJSONString());
//        }

        return  entries;
    }

    public void start() {
        if(timer == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    counter++;
                    sync();
                }
            };

            timer.schedule(timerTask, 1000, 12000);
        }
    }

    public void stop() {
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
        }
    }
}
