package com.cnezsoft.zentao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;

import java.util.HashMap;
import java.util.Map;

/**
 * Main activity
 */
public class MainActivity extends ZentaoActivity {

    /**
     * Used to store Zentao application context
     */
    private ZentaoApplication application;
    private User user;
    private LinearLayout summeryListContainer;
    private BroadcastReceiver syncReceiver = null;
    private IntentFilter intentFilter = null;

    @Override
    public void onPause() {
        if(syncReceiver != null) {
            this.unregisterReceiver(syncReceiver);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(intentFilter == null) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(Synchronizer.MESSAGE_OUT_SYNC);
        }

        if(syncReceiver == null) {
            syncReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals(Synchronizer.MESSAGE_OUT_SYNC)) {
                        new UpdateSummeries().execute(context);
                    }
                }
            };
        }

        this.registerReceiver(syncReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check user status
        application = (ZentaoApplication) getApplicationContext();
        user = application.getUser();
        application.checkUserStatus(this);
        final Context context = this;

        // hello to user
        user.setOnUserInfoChangeListener(new User.OnUserInfoChangeListener() {
            @Override
            public void onUserInfoChange(String name) {
                if(name.equals(User.ACCOUNT) || name.equals(User.REALNAME)) {
                    ((TextView) findViewById(R.id.text_hello_user)).setText(user.getHelloText(context));
                }
            }
        });
        ((TextView) findViewById(R.id.text_hello_user)).setText(user.getHelloText(this));
        summeryListContainer = (LinearLayout) findViewById(R.id.container_summery_list);
        new UpdateSummeries().execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            application.openActivity(this, AppNav.setting);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ZentaoApplication.LOGIN_REQUEST)
        {
            // Show message
            Toast.makeText(application, getText(resultCode == RESULT_OK ? R.string.message_login_success : R.string.message_login_failed), Toast.LENGTH_SHORT).show();

            // Start syncService
            startService(new Intent(this, ZentaoSyncService.class));
        }
    }

    private void updateSummeries(HashMap<EntryType, HashMap<String, String>> summeries) {
        for(Map.Entry<EntryType, HashMap<String, String>> entry: summeries.entrySet()) {
            EntryType type = entry.getKey();
            HashMap<String, String> summery = entry.getValue();
            ViewGroup summeryContainer = (ViewGroup) summeryListContainer.findViewWithTag("container_summery_" + type.name().toLowerCase());
            if(summeryContainer != null) {
                TextView iconView = (TextView) summeryContainer.findViewWithTag("icon_summery");
                iconView.setText("{fa-" + type.icon() + "}");
                iconView.setTextColor(type.accent().color(MaterialColorName.C500).value());

                ((TextView) summeryContainer.findViewWithTag("text_summery_heading")).setText(ZentaoApplication.getEnumText(this, type));
                ((TextView) summeryContainer.findViewWithTag("text_summery_newest")).setText(summery.get("newest"));

                TextView numberView = (TextView) summeryContainer.findViewWithTag("text_summery_number");
                numberView.setTextColor(type.accent().color(MaterialColorName.A700).value());
                TextView numberNameView = (TextView) summeryContainer.findViewWithTag("text_summery_number_name");

                String unread = summery.get("unread");
                if(!unread.equals("0")) {
                    numberView.setText(unread);
                    numberNameView.setText(getString(R.string.text_new_items));
                    summeryContainer.setBackgroundColor(MaterialColorSwatch.Yellow.color(MaterialColorName.C100).value());
                } else {
                    String count = summery.get("count");
                    numberView.setText(count);
                    if(count.equals("0")) {
                        numberView.setTextColor(getResources().getColor(R.color.secondary_text));
                    }
                    numberNameView.setText(getString(type == EntryType.Todo ? R.string.text_undone : R.string.text_assigned_to));
                    summeryContainer.setBackgroundColor(Color.WHITE);
                }
            }
        }
    }

    public void openListActivity(View view) {
        switch (view.getTag().toString()) {
            case "container_summery_todo":
                application.openActivity(this, AppNav.todo);
                break;
            case "container_summery_task":
                application.openActivity(this, AppNav.task);
                break;
            case "container_summery_bug":
                application.openActivity(this, AppNav.bug);
                break;
            case "container_summery_story":
                application.openActivity(this, AppNav.story);
                break;
        }
    }

    private class UpdateSummeries extends AsyncTask<Context, Integer, HashMap<EntryType, HashMap<String, String>>> {
        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(user.getStatus() == User.Status.Unknown) {
                cancel(true);
            }
        }

        @Override
        protected HashMap<EntryType, HashMap<String, String>> doInBackground(Context... params) {
            DAO dao = new DAO(params[0]);
            return dao.getSummery(user.getAccount());
        }

        @Override
        protected void onPostExecute(HashMap<EntryType, HashMap<String, String>> entryTypeHashMapHashMap) {
            super.onPostExecute(entryTypeHashMapHashMap);
            updateSummeries(entryTypeHashMapHashMap);
        }
    }

    public void showDatabase(View view) {
        String logKey = "DATABASE TEST";
        long counter;
        DAO dao = new DAO(this);

        Log.d(logKey, "*************** show database ***************");
        for(EntryType type: new EntryType[] {EntryType.Todo, EntryType.Task}) {
            counter = dao.count(type);
            Log.d(logKey, type.name() + " count: " + counter);
            if(counter > 0) {
                final Cursor cursor = dao.query(type);
                while (cursor.moveToNext()) {
                    Log.d(logKey, type.name() + ": " + new DataEntry(type){{fromCursor(cursor);}}.toJSONString());
                }
            }
            Log.d(logKey, "---------------------------------------------");
        }
    }
}
