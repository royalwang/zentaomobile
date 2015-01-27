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
 * Zentao Synchronizer
 *
 * Created by Catouse on 2015/1/19.
 */
public class Synchronizer {
    private Context context;
    private User user;
    private ZentaoConfig zentaoConfig;

    private Timer timer;
    private TimerTask timerTask;
    private final int maxLoginInterval = 1000*60*2;
    private long lastLoginTime = 0;

    /**
     * Constructor with context
     * @param context
     */
    public Synchronizer(Context context) {
        this.context = context;
        ZentaoApplication application = (ZentaoApplication) context.getApplicationContext();
        user = application.getUser();
        zentaoConfig = application.getZentaoConfig();
    }

    /**
     * Sync data
     * @return
     */
    public boolean sync() {
        User.Status userStatus = user.getStatus();
        Log.v("SYNC", "userStatus: " + userStatus.toString());
        if(userStatus == User.Status.Offline) {
            Long thisLoginTime = new Date().getTime();
            if((thisLoginTime - lastLoginTime) < maxLoginInterval) {
                Log.w("SYNC", "Login required, but abort this time to prevent server lock.");
                return false;
            } else {
                lastLoginTime = thisLoginTime;
            }
            Log.v("SYNC", "The user is offline, now login again.");
            ZentaoApplication application = (ZentaoApplication) context.getApplicationContext();
            if(application.login()) {
                user = application.getUser();
                zentaoConfig = application.getZentaoConfig();
            } else {
                return false;
            }
        }
        else if(userStatus == User.Status.Unknown) {
            Log.w("SYNC", "Unknown user, sync stopped!");
            return false;
        }

        Date thisSyncTime = new Date();

        OperateBundle<Boolean, JSONObject> result = ZentaoAPI.getDataList(zentaoConfig, user);
        if(result.getResult()) {
            JSONObject data = result.getValue();
            Log.v("SYNC", "success: " + result.getMessage() + ", data: " + (data != null ? data.toString() : "no data."));

            if(data != null) {
                DAO dao = new DAO(context);
                OperateResult<Boolean> daoResult = dao.save(getEntriesFromJSON(data));
                dao.close();
                Log.v("SYNC", "dao result: " + daoResult.toString());
            }

            user.setLastSyncTime(thisSyncTime).save();
            return true;
        }

        Log.v("SYNC", "result: " + "false");
        return false;
    }

    /**
     * Get entries from JSON data
     * @param jsonData
     * @return
     */
    private ArrayList<DataEntry> getEntriesFromJSON(JSONObject jsonData) {
        Log.v("SYNC", "getEntriesFromJSON: " + jsonData.toString());
        ArrayList<DataEntry> entries = new ArrayList<>();
        String name;
        EntryType entryType;
        DataEntry entry;
        JSONObject data;
        JSONArray set, deletes;
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
                    deletes = data.getJSONArray("delete");
                    for(int i = 0; i < deletes.length(); ++i) {
                        entries.add(new DataEntry(entryType) {{markDeleting();}});
                    }

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

        return  entries;
    }

    /**
     * Start sync
     */
    public void start() {
        if(timer == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    sync();
                }
            };

            timer.schedule(timerTask, 1000, 12000);
        }
    }

    /**
     * Stop sync
     */
    public void stop() {
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
        }
    }
}
