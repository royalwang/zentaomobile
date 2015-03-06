package com.cnezsoft.zentao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.cnezsoft.zentao.data.Bug;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DAOOperateInfo;
import com.cnezsoft.zentao.data.DAOResult;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.DataEntryFactory;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Product;
import com.cnezsoft.zentao.data.Project;
import com.cnezsoft.zentao.data.Story;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Zentao Synchronizer
 *
 * Created by Catouse on 2015/1/19.
 */
public class Synchronizer extends BroadcastReceiver {
    public static final String MESSAGE_IN_SYNC = "com.cnezsoft.zentao.MESSAGE_IN_SYNC";
    public static final String MESSAGE_OUT_SYNC = "com.cnezsoft.zentao.MESSAGE_OUT_SYNC";
    public static final String MESSAGE_IN_GET_ENTRY = "com.cnezsoft.zentao.MESSAGE_IN_GET_ENTRY";
    public static final String MESSAGE_OUT_GET_ENTRY = "com.cnezsoft.zentao.MESSAGE_OUT_GET_ENTRY";
    public static final String MESSAGE_IN_SYNC_RESTART = "com.cnezsoft.zentao.MESSAGE_IN_SYNC_RESTART";
    public static final String MESSAGE_OUT_SYNC_RESTART = "com.cnezsoft.zentao.MESSAGE_OUT_SYNC_RESTART";

    private Context context;

    private Timer timer;
    private TimerTask timerTask;
    private final int maxLoginInterval = 1000*60*2;
    private long lastLoginTime = 0;
    private boolean running;
    private long lastSyncFreg;
    private ZentaoApplication application;
    private int minIdKey = Integer.MAX_VALUE;
    private int itemCount = 0;
    private boolean checkDatabaseEmpty = true;

    /**
     * Constructor with context
     * @param context
     */
    public Synchronizer(Context context) {
        this.context = context;
        application = (ZentaoApplication) context.getApplicationContext();
    }

    /**
     * Sync data
     * @return
     */
    public boolean sync(EntryType entryType) {
        if(!application.checkLogin()) return false;

        User user = application.getUser();
        if(checkDatabaseEmpty) {
            DAO dao = new DAO(context);
            checkDatabaseEmpty = dao.isDatabaseEmpty();
            dao.close();

            if(checkDatabaseEmpty) {
                user.put(UserAttr.lastSyncTime, new Date(0));
                application.saveUser();
            }
        }

        if(user.withIncrementSync()) {
            Date thisSyncTime = new Date();
            OperateBundle<Boolean, JSONObject> result = ZentaoAPI.getDataList(user, entryType);
            if(saveData(result)) {
                user.put(UserAttr.lastSyncTime, thisSyncTime);
                application.saveUser();
                return true;
            }
            return false;
        }

        return deepSync();
    }

    private boolean deepSync() {
        HashMap<EntryType, Integer> deepSyncConfig = new HashMap<EntryType, Integer>(){{
            for(EntryType entryType: EntryType.values()) {
                if(entryType != EntryType.Default) {
                    put(entryType, 0);
                }
            }
        }};
        int range;
        EntryType entryType;
        OperateBundle<Boolean, JSONObject> result;
        boolean needMoreRequest = true;
        Date thisSyncTime = new Date();
        User user = application.getUser();
        while (needMoreRequest) {
            needMoreRequest = false;
            for(Map.Entry<EntryType, Integer> entry: deepSyncConfig.entrySet()) {
                entryType = entry.getKey();
                range = entry.getValue();

                if(range < 0) continue;

                result = ZentaoAPI.getDataList(user, "increment", entryType, range, 500, "index");
                if(saveData(result)) {
                    if(minIdKey == Integer.MAX_VALUE || itemCount < 500) {
                        range = -1;
//                        deepSyncConfig.remove(entryType);
                    } else {
                        range = minIdKey;
                        needMoreRequest = true;
                    }
                    entry.setValue(range);
                } else {
                    return false;
                }
            }
        }
        user.put(UserAttr.lastSyncTime, thisSyncTime);
        application.saveUser();
        return true;
    }

    /**
     * Save sync data
     * @param result
     * @return
     */
    private boolean saveData(OperateBundle<Boolean, JSONObject> result) {
        if(result.getResult()) {
            JSONObject data = result.getValue();
            // Log.v("SYNC", "success: " + result.getMessage() + ", data: " + (data != null ? data.toString() : "no data."));

            if(data != null) {
                DAO dao = new DAO(context);
                boolean withIncrementSync = application.getUser().withIncrementSync();
                DAOResult daoResult = dao.save(getEntriesFromJSON(data), withIncrementSync);
                dao.close();
                if(withIncrementSync) {
                    handleNotify(daoResult);
                }
                // Log.v("SYNC", daoResult.toString());
            }
            return true;
        }

        return false;
    }

    /**
     * Handle notify
     * @param daoResult
     * @return
     */
    public boolean handleNotify(DAOResult daoResult) {
//        if(!application.isRunningInBackground()) {
//            return false;
//        }
        for(Map.Entry<EntryType, DAOOperateInfo> result: daoResult.getInfos().entrySet()) {
            if(result.getValue().add() > 0) {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.drawable.zentao_icon_flat_white);
                builder.setContentTitle(String.format(context.getString(R.string.text_notify_new_items_format), result.getValue().add(),
                        ZentaoApplication.getEnumText(context, result.getKey())))
                        .setNumber(result.getValue().add())
                        .setContentText(context.getString(R.string.text_click_view));

                Intent resultIntent = new Intent(context, MainActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager = (NotificationManager)
                        context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                notificationManager.notify(0, builder.build());
                return true;
            }
        }
        return false;
    }

    /**
     * Sync data
     * @return
     */
    public boolean sync() {
        return sync(EntryType.Default);
    }

    /**
     * Get entry from server
     * @param entryType
     * @param id
     * @return
     */
    public DataEntry getEntry(EntryType entryType, String id) {
        User user = application.getUser();
        User.Status userStatus = user.getStatus();
        Log.v("SYNC", "getEntry " + entryType + ":" + id);
        Log.v("SYNC", "userStatus: " + userStatus.toString());
        if(userStatus == User.Status.OFFLINE) {
            Long thisLoginTime = new Date().getTime();
            if((thisLoginTime - lastLoginTime) < maxLoginInterval) {
                Log.w("SYNC", "Login required, but abort this time to prevent server lock.");
                return null;
            } else {
                lastLoginTime = thisLoginTime;
            }
            ZentaoApplication application = (ZentaoApplication) context.getApplicationContext();
            if(application.login()) {
                user = application.getUser();
            } else {
                return null;
            }
        } else if(userStatus == User.Status.UNKNOWN) {
            // Log.w("SYNC", "Unknown user, sync stopped!");
            return null;
        }

        OperateBundle<Boolean, JSONObject> result = ZentaoAPI.getDataItem(user, entryType, id);
        if(result.getResult()) {
            JSONObject data = result.getValue();
            // Log.v("SYNC", "success: " + result.getMessage() + ", data: " + (data != null ? data.toString() : "no data."));

            if(data != null) {
                final DataEntry entry = DataEntryFactory.create(entryType, data);
                entry.setLastSyncTime();
                if(entry.getType() == EntryType.Todo) {
                    entry.put(TodoColumn.account, user.getAccount());
                }
                DAO dao = new DAO(context);
                OperateResult<Boolean> daoResult = dao.save(new ArrayList<DataEntry>(1){{add(entry);}});
                dao.close();
                // Log.v("SYNC", daoResult.toString());
                return entry;
            }

            return null;
        }

        // Log.v("SYNC", "result: false, message: " + result.getMessage());
        return null;
    }

    /**
     * Get entries from JSON data
     * @param jsonData
     * @return
     */
    private ArrayList<DataEntry> getEntriesFromJSON(JSONObject jsonData) {
        // set minIdKey to Integer.MAX_VALUE;
        minIdKey = Integer.MAX_VALUE;
        itemCount = 0;

        Log.v("SYNC", "getEntriesFromJSON: " + jsonData.toString());
        ArrayList<DataEntry> entries = new ArrayList<>();
        String name;
        EntryType entryType;
        DataEntry entry;
        JSONObject data;
        JSONArray set;
        JSONArray deletes;
        String[] keys;
        int setLength;
        String account = application.getUser().getAccount();
        Iterator<String> names = jsonData.keys();
        while (names.hasNext()) {
            name = names.next();
            entryType = EntryType.fromName(name);
            if(entryType != null) {

                try {
                    data = jsonData.getJSONObject(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("SYNC", "'" + name + "' JSON data's format is not correct.");
                    continue;
                }

                try {
                    deletes = data.getJSONArray("delete");
                    for(int i = 0; i < deletes.length(); ++i) {
                        entry = new DataEntry(entryType);
                        entry.key(deletes.getInt(i) + "");
                        entry.markDeleting();
                        entries.add(entry);
                    }
                } catch (JSONException e) {
                }

                try {
                    set = data.getJSONArray("set");
                    setLength = set.length();
                    if(setLength <= 0) continue;

                    keys = Helper.getStringArrayFromJSON(data.getJSONArray("key"));

                    switch (entryType) {
                        case Todo:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Todo(set.getJSONArray(i), keys);
                                entry.put(TodoColumn.account, account);
                                entries.add(entry);
                                itemCount++;
                                minIdKey = Math.min(minIdKey, Integer.parseInt(entry.key()));
                            }
                            break;
                        case Task:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Task(set.getJSONArray(i), keys);
                                entries.add(entry);
                                itemCount++;
                                minIdKey = Math.min(minIdKey, Integer.parseInt(entry.key()));
                            }
                            break;
                        case Bug:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Bug(set.getJSONArray(i), keys);
                                entries.add(entry);
                                itemCount++;
                                minIdKey = Math.min(minIdKey, Integer.parseInt(entry.key()));
                            }
                            break;
                        case Story:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Story(set.getJSONArray(i), keys);
                                entries.add(entry);
                                itemCount++;
                                minIdKey = Math.min(minIdKey, Integer.parseInt(entry.key()));
                            }
                            break;
                        case Product:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Product(set.getJSONArray(i), keys);
                                entries.add(entry);
                                itemCount++;
                                minIdKey = Math.min(minIdKey, Integer.parseInt(entry.key()));
                            }
                            break;
                        case Project:
                            for (int i = 0; i < setLength; ++i) {
                                entry = new Project(set.getJSONArray(i), keys);
                                entries.add(entry);
                                itemCount++;
                                minIdKey = Math.min(minIdKey, Integer.parseInt(entry.key()));
                            }
                            break;
                    }
                } catch (JSONException e) {
                }
            }
        }

        // todo items
        // Log.v("SYNC", entries.size() + " data to save in the database.");

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
                    Intent intent = new Intent(MESSAGE_OUT_SYNC);
                    intent.putExtra("result", sync());
                    intent.putExtra("auto", true);
                    context.sendBroadcast(intent);
                }
            };
            User user = application.getUser();
            lastSyncFreg = user.getSyncFrequency();
            timer.schedule(timerTask, 1000, lastSyncFreg);
            // Log.v("SYNC", "start " + lastSyncFreg);
            running = true;
        } else {
            Log.w("SYNC", "start failed. Task is already running.");
        }
    }

    /**
     * Stop sync
     */
    public void stop() {
        // Log.v("SYNC", "stop " + lastSyncFreg);
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        running = false;
    }

    /**
     * Restart
     */
    public void restart() {
        stop();
        start();
        // Log.v("SYNC", "restart");
    }

    /**
     * Handle things on receive
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        new HandleMessage().execute(intent);
    }

    /**
     * The async task for login in Zentao server
     */
    private class HandleMessage extends AsyncTask<Intent, Integer, Intent> {

        protected Intent doInBackground(Intent... intents) {
            Intent intent = intents[0];
            Intent intentOut = null;
            String entryTypeStr;
            String action = intent.getAction();
            switch (action) {
                case MESSAGE_IN_SYNC:
                    EntryType entryType = EntryType.Default;
                    entryTypeStr = intent.getStringExtra("type");
                    if(entryTypeStr != null) {
                        entryType = EntryType.valueOf(entryTypeStr);
                    }
                    intentOut = new Intent(MESSAGE_OUT_SYNC);
                    intentOut.putExtra("result", sync(entryType));
                    break;
                case MESSAGE_IN_GET_ENTRY:
                    DataEntry entry = null;
                    entryTypeStr = intent.getStringExtra("type");
                    String id = intent.getStringExtra("id");
                    if(entryTypeStr != null && id != null) {
                        entry = getEntry(EntryType.valueOf(entryTypeStr), id);
                    }

                    intentOut = new Intent(MESSAGE_OUT_GET_ENTRY);
                    intentOut.putExtra("result", entry != null);
                    break;
                case MESSAGE_IN_SYNC_RESTART:
                    application.getUser(true);
                    restart();
                    intentOut = new Intent(MESSAGE_OUT_SYNC_RESTART);
                    break;
            }
            return intentOut;
        }

        protected void onPostExecute(Intent intent) {
            if(intent != null) {
                try {
                    ZentaoActivity zentaoActivity = (ZentaoActivity) context;
                    zentaoActivity.onReceiveMessage(intent);
                } catch (ClassCastException e) {
                    context.sendBroadcast(intent);
                }
            }
        }
    }

}
