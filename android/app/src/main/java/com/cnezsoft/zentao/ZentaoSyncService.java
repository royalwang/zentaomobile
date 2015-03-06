package com.cnezsoft.zentao;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
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
        if(syncer == null) {
            syncer = new Synchronizer(this);
        }
        syncer.start();

        // Register receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Synchronizer.MESSAGE_IN_GET_ENTRY);
        intentFilter.addAction(Synchronizer.MESSAGE_IN_SYNC);
        intentFilter.addAction(Synchronizer.MESSAGE_IN_SYNC_RESTART);
        registerReceiver(syncer, intentFilter);

        Log.v("SYNC", "Zentao sync server is running.");
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        Log.v("SYNC", "Zentao sync server destroyed.");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
