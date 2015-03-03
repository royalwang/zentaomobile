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
import com.cnezsoft.zentao.data.Product;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ProductActivity
 * Created by Catouse on 2015/2/28.
 */
public class ProductActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAccentSwatch(EntryType.Product.accent());
    }

    /**
     * Load list data in async task.
     *
     * @param params
     * @return
     */
    @Override
    protected ArrayList<HashMap<String, Object>> loadData(Context... params) {
        DAO dao = new DAO(params[0]);
        ArrayList<HashMap<String, Object>> result = dao.getProductsList(false);
        dao.close();
        return result;
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
        ((ZentaoApplication) getApplicationContext()).openDetailActivity(this, EntryType.Product, (int) (((HashMap<String, Object>) adapter.getItem(position)).get("_id")));
    }

    @Override
    protected HashMap<String, Object> convertListItemData(HashMap<String, Object> item) {
        int id = (int) item.get("id");
        item.put("_id", id);
        item.put("icon_back", new ControlBindInfo("{fa-circle}", Product.accent(id).primary().value()));
        item.put("icon", "{fa-cube}");
        long bugCount = (long) item.get("bugCount");
        item.put("tag", bugCount > 0 ? new ControlBindInfo("{fa-bug} " + bugCount, MaterialColorSwatch.Red.primary().value()) : new ControlBindInfo(View.GONE));
        long storyCount = (long) item.get("storyCount");
        item.put("id", String.format(getString(R.string.text_story_count_format), storyCount));
        long changeCount = (long) item.get("changedCount");
        long draftCount = (long) item.get("draftCount");
        String info = "";
        if(changeCount > 0) {
            info += String.format(getString(R.string.text_changed_story_count_format), changeCount) + "    ";
        }
        if(draftCount > 0) {
            info += String.format(getString(R.string.text_draft_story_count_format), draftCount) + "    ";
        }
        item.put("info", info);
        item.put("status", new ControlBindInfo(View.GONE));
        return item;
    }

    @Override
    protected boolean onUpdateList(ArrayList<HashMap<String, Object>> newData) {
        boolean result = super.onUpdateList(newData);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(result ? String.format(getString(R.string.text_product_title_format), newData.size()) : getString(R.string.title_activity_product));
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_closed_product) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
