package com.cnezsoft.zentao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main activity
 */
public class MainActivity extends ZentaoActivity {

    class TextWithColor {
        public String text;
        public int color;

        TextWithColor(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    /**
     * Used to store Zentao application context
     */
    private ZentaoApplication application;
    private User user;
    private ListView dashboardList;
    private SimpleAdapter dashboardAdapter;
    private ArrayList<HashMap<String, ?>> dashboardItems;
    private BroadcastReceiver syncReceiver = null;
    private ArrayList<String> messages;

    @Override
    public void onResume() {
        super.onResume();

        ((TextView) findViewById(R.id.text_hello_user)).setText(user.getHelloText(this));
        ((TextView) findViewById(R.id.text_comany)).setText(user.getString(UserAttr.company));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check user status
        application = (ZentaoApplication) getApplicationContext();
        final Context context = this;

        // hello to user
        application.setOnUserAttrChangeListener(new String[]{UserAttr.account.name(), UserAttr.realName.name()}, new UserPreferences.OnUserAttrChangeListener() {
            @Override
            public void onUserAttrChange(String name, Object value) {
                ((TextView) findViewById(R.id.text_hello_user)).setText(user.getHelloText(context));
            }
        });

        application.login(this, null);

        dashboardItems = new ArrayList<>();
        dashboardList = (ListView) findViewById(R.id.list_dashboard);
        dashboardAdapter = new SimpleAdapter(this, dashboardItems, R.layout.list_item_dashboard,
                new String[]{"icon",    "title",         "subtitle",         "number",         "numberName"},
                new    int[]{R.id.icon, R.id.text_title, R.id.text_subtitle, R.id.text_number, R.id.text_number_name});
        dashboardAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                switch (view.getId()) {
                    case R.id.text_number:
                    case R.id.icon:
                        TextWithColor iconData = (TextWithColor) data;
                        TextView iconView = (TextView) view;
                        iconView.setText(iconData.text);
                        iconView.setTextColor(iconData.color);
                        return true;
                }
                return false;
            }
        });
        dashboardList.setAdapter(dashboardAdapter);
        dashboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openListActivity((EntryType)((HashMap<String, Object>) dashboardAdapter.getItem(position)).get("type"));
            }
        });

        listenMessage(Synchronizer.MESSAGE_OUT_SYNC);
        updateSummeries();
    }

    @Override
    protected void onReceiveMessage(Intent intent) {
        if(intent.getAction().equals(Synchronizer.MESSAGE_OUT_SYNC)) {
            updateSummeries();
        }
    }

    private void updateSummeries() {
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

            if(resultCode == RESULT_OK) {
                new UpdateSummeries().execute(this);
            }
        }
    }

    private void updateSummeries(ArrayList<HashMap<String, Object>> summeries) {
        dashboardItems.clear();
        for(HashMap<String, Object> summery: summeries) {
            EntryType type = (EntryType) summery.get("type");
            summery.put("icon", new TextWithColor("{fa-" + type.icon() + "}", type.accent().color(MaterialColorName.C500).value()));
            summery.put("title", ZentaoApplication.getEnumText(this, type));
            summery.put("subtitle", summery.get("newest"));

            long newCount = (long) summery.get("newCount");
            if(newCount > 0) {
                summery.put("number", new TextWithColor(newCount + "", type.accent().color(MaterialColorName.A700).value()));
                switch (type) {
                    case Product:
                        summery.put("numberName", getString(R.string.text_normal_product));
                        break;
                    case Project:
                        summery.put("numberName", getString(R.string.text_doing_project));
                        break;
                    default:
                        summery.put("numberName", String.format(getString(R.string.text_new_item_format), ZentaoApplication.getEnumText(this, type)));
                }
            } else {
                summery.put("number", new TextWithColor(summery.get("count").toString(), MaterialColorSwatch.Grey.color(MaterialColorName.C700).value()));
                switch (type) {
                    case Product:
                        summery.put("numberName", getString(R.string.text_all_product));
                        break;
                    case Project:
                        summery.put("numberName", getString(R.string.text_all_project));
                        break;
                    case Todo:
                        summery.put("numberName", getString(R.string.text_undone));
                        break;
                    default:
                        summery.put("numberName", getString(R.string.text_assigned_to));
                }
            }
            dashboardItems.add(summery);
        }
        dashboardAdapter.notifyDataSetChanged();
    }

    public void openListActivity(EntryType type) {
        switch (type) {
            case Todo:
                application.openActivity(this, AppNav.todo);
                break;
            case Task:
                application.openActivity(this, AppNav.task);
                break;
            case Bug:
                application.openActivity(this, AppNav.bug);
                break;
            case Story:
                application.openActivity(this, AppNav.story);
                break;
            case Project:
                application.openActivity(this, AppNav.project);
                break;
            case Product:
                application.openActivity(this, AppNav.product);
                break;

        }
    }

    public void onCompanyClick(View view) {
        showDatabase(null);
    }

    private class UpdateSummeries extends AsyncTask<Context, Integer, ArrayList<HashMap<String, Object>>> {
        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            user = application.getUser();
            if(user.getStatus() == User.Status.UNKNOWN) {
                cancel(true);
            }
        }

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(Context... params) {
            DAO dao = new DAO(params[0]);
            ArrayList<HashMap<String, Object>> result = dao.getSummery(user.getAccount());
            dao.close();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> entryTypeHashMapHashMap) {
            super.onPostExecute(entryTypeHashMapHashMap);
            updateSummeries(entryTypeHashMapHashMap);
        }
    }

    public void showDatabase(EntryType entryType) {
        String logKey = "DATABASE TEST";
        EntryType[] types;
        if(entryType == null || entryType == EntryType.Default) {
            types = new EntryType[] {EntryType.Todo, EntryType.Task, EntryType.Bug, EntryType.Story, EntryType.Project, EntryType.Product};
        } else {
            types = new EntryType[] {entryType};
        }

        long counter;
        DAO dao = new DAO(this);

        Log.d(logKey, "*************** show database ***************");
        for(EntryType type: types) {
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
