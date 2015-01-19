package com.cnezsoft.zentao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ZentaoSyncService extends Service {
    private Synchronizer syncer;

    public ZentaoSyncService() {
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {

        super.onCreate();
        syncer = new Synchronizer(this);
        syncer.start();
        Log.v("SYNC", "禅道服务启动");
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("SYNC", "禅道服务销毁");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
