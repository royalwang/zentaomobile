package com.cnezsoft.zentao;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import java.util.ArrayList;

/**
 * Created by Catouse on 2015/1/29.
 */
public class ZentaoActivity extends ActionBarActivity {
    private BroadcastReceiver syncReceiver = null;
    private ArrayList<String> messages;
    private MaterialColorSwatch swatch;

    protected MaterialColorSwatch getSwatch() {
        return swatch;
    }

    /**
     * List global message (Call this in onCreate method)
     * @param message
     */
    protected void listenMessage(String message) {
        if(messages == null) {
            messages = new ArrayList<>(1);
        }
        messages.add(message);
    }

    protected void listenMessage(String... newMessages) {
        if(newMessages != null) {
            if(messages == null) {
                messages = new ArrayList<>(1);
            }
            for(String message: newMessages) {
                messages.add(message);
            }
        }
    }

    @Override
    protected void onPause() {
        if(syncReceiver != null) {
            this.unregisterReceiver(syncReceiver);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (messages != null && messages.size() > 0) {
            IntentFilter intentFilter = new IntentFilter();
            for(String message: messages) {
                intentFilter.addAction(message);
            }

            if (syncReceiver == null) {
                syncReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        onReceiveMessage(intent);
                    }
                };
            }

            this.registerReceiver(syncReceiver, intentFilter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setElevation(0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setAccentSwatch(MaterialColorSwatch swatch) {
        this.swatch = swatch;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(swatch.color(MaterialColorName.C600).value()));
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            window.setStatusBarColor(swatch.color(MaterialColorName.C600).value());
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            Log.w("ZentaoActivity", "Can't set status bar color.");
        }
    }

    protected void onReceiveMessage(Intent intent) {
    }

    protected TextView findTextViewById(int id) {
        try {
            return (TextView) findViewById(id);
        } catch (ClassCastException e) {
            return null;
        }
    }

    protected ListView findListViewById(int id) {
        try {
            return (ListView) findViewById(id);
        } catch (ClassCastException e) {
            return null;
        }
    }
}
