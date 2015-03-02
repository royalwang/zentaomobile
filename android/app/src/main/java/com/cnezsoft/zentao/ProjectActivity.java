package com.cnezsoft.zentao;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Project;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ProjectActivity extends SimpleListActivity {

    NumberFormat percentageFormat;
    NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        percentageFormat = NumberFormat.getPercentInstance();
        percentageFormat.setMaximumFractionDigits(1);
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(1);
        super.onCreate(savedInstanceState);

        setAccentSwatch(EntryType.Project.accent());
    }

    /**
     * On list item click
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    protected void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((ZentaoApplication) getApplicationContext()).openDetailActivity(this, EntryType.Project, (int) (((HashMap<String, Object>) adapter.getItem(position)).get("_id")));
    }

    @Override
    protected ArrayList<HashMap<String, Object>> loadData(Context... params) {
        DAO dao = new DAO(params[0]);
        ArrayList<HashMap<String, Object>> result = dao.getProjectsList(false);
        dao.close();
        return result;
    }

    @Override
    protected HashMap<String, Object> convertListItemData(HashMap<String, Object> item) {
        int id = (int) item.get("id");
        item.put("_id", id);
        float progress = (float) item.get("progress");
        item.put("icon_back", new ControlBindInfo("{fa-folder}", Project.accent(id).primary().value()));
        item.put("id", new ControlBindInfo(percentageFormat.format(progress), progress >= 1 ? MaterialColorSwatch.Green.primary().value() : MaterialColorSwatch.Red.primary().value()));
        float hours = (float) item.get("hours");
        item.put("status", numberFormat.format(hours) + "h");
        long bugCount = (long) item.get("bugCount");
        item.put("tag", bugCount > 0 ? new ControlBindInfo("{fa-bug} " + bugCount, MaterialColorSwatch.Red.primary().value()) : new ControlBindInfo(View.GONE));
        String title = (String) item.get("title");
        item.put("icon", Helper.isNullOrEmpty(title) ? "{fa-folder-o}" : title.substring(0, 1));
        item.put("info", item.get("date"));
        return item;
    }

    @Override
    protected boolean onUpdateList(ArrayList<HashMap<String, Object>> newData) {
        boolean result = super.onUpdateList(newData);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(result ? String.format(getString(R.string.text_project_title_format), newData.size()) : getString(R.string.title_activity_project));
        }
        return result;
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
        if (id == R.id.action_show_all_items) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
