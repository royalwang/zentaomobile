package com.cnezsoft.zentao;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.Date;
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
        User.Status userStatus = user.getStatus();
        if(userStatus == User.Status.Offline) {
            Long thisLoginTime = new Date().getTime();
            if((thisLoginTime - lastLoginTime) < maxLoginInterval) {
                Log.v("SYNC", "虽然需要重新登录，但为了避免频繁登录，取消了尝试");
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

        OperateBundle<Boolean, JSONObject> result = ZentaoAPI.getDataList(zentaoConfig, user, lastSyncTime);
        if(result.getResult()) {
            JSONObject data = result.getValue();
            Log.v("SYNC", "result: true, data: " + data.toString());
            lastSyncTime = new Date();
            return true;
        }
        Log.v("SYNC", "result: " + "false");

        return false;
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
