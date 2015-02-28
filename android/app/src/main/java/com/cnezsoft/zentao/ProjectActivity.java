package com.cnezsoft.zentao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.ControlBindInfo;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.EntryType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class ProjectActivity extends ZentaoActivity {

    private ListView list;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, ?>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setAccentSwatch(EntryType.Project.accent());

        data = new ArrayList<>();
        list = (ListView) findViewById(R.id.list_project);
        adapter = new SimpleAdapter(this, data, R.layout.list_item_project,
                new String[]{"color",   "icon",         "title",         "progress",   "date",         "hours",          "tag"},
                new    int[]{R.id.icon, R.id.text_icon, R.id.text_title, R.id.text_id, R.id.text_info, R.id.text_status, R.id.text_tag});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                switch (view.getId()) {
                    case R.id.icon:
                        TextView iconView = (TextView) view;
                        iconView.setTextColor((int) data);
                        iconView.setText("{fa-folder}");
                        return true;
                    case R.id.text_id:
                        TextView progressView = (TextView) view;
                        ControlBindInfo progressInfo = (ControlBindInfo) data;
                        progressView.setTextColor(progressInfo.textColor);
                        progressView.setText(progressInfo.text);
                        return true;
                    case R.id.text_tag:
                        TextView tagView = (TextView) view;
                        long bugCount = (long) data;
                        tagView.setVisibility(bugCount > 0 ? View.VISIBLE : View.GONE);
                        if(bugCount > 0) {
                            tagView.setTextColor(MaterialColorSwatch.Red.primary().value());
                            tagView.setBackgroundColor(Color.TRANSPARENT);
                            tagView.setText("{fa-bug} " + bugCount);
                        }
                        return true;
                }
                return false;
            }
        });
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openListActivity((EntryType)((HashMap<String, Object>) dashboardAdapter.getItem(position)).get("type"));
            }
        });

        listenMessage(Synchronizer.MESSAGE_OUT_SYNC);
        executeUpdateListTask();
    }

    @Override
    protected void onReceiveMessage(Intent intent) {
        if(intent.getAction().equals(Synchronizer.MESSAGE_OUT_SYNC)) {
            executeUpdateListTask();
        }
    }

    private void executeUpdateListTask() {
        new UpdateListTask().execute(this);
    }

    private void onUpdateList(ArrayList<HashMap<String, Object>> newData) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(newData != null && newData.size() > 0) {
            data.clear();
            NumberFormat percentageFormat = NumberFormat.getPercentInstance();
            percentageFormat.setMaximumFractionDigits(1);
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(1);
            MaterialColorSwatch[] colors = MaterialColorSwatch.values();
            for(HashMap<String, Object> item: newData) {
                float progress = (float) item.get("progress");
                item.put("progress", new ControlBindInfo(percentageFormat.format(progress), progress >= 1 ? MaterialColorSwatch.Green.primary().value() : MaterialColorSwatch.Red.primary().value()));
                float hours = (float) item.get("hours");
                item.put("hours", numberFormat.format(hours) + "h");
                item.put("tag", item.get("bugCount"));
                String title = (String) item.get("title");
                item.put("icon", Helper.isNullOrEmpty(title) ? "{fa-folder-o}" : title.substring(0, 1));
                int id = (int) item.get("id");
                item.put("color", colors[id%colors.length].primary().value());
                data.add(item);
            }
            adapter.notifyDataSetChanged();
            if(actionBar != null) {
                actionBar.setTitle(String.format(getString(R.string.text_project_title_format), newData.size()));
            }
        } else if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_activity_project));
        }
    }

    private class UpdateListTask extends AsyncTask<Context, Integer, ArrayList<HashMap<String, Object>>> {
        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(Context... params) {
            DAO dao = new DAO(params[0]);
            ArrayList<HashMap<String, Object>> result = dao.getProjectsList(false);
            dao.close();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> entryTypeHashMapHashMap) {
            super.onPostExecute(entryTypeHashMapHashMap);
            onUpdateList(entryTypeHashMapHashMap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
