package com.cnezsoft.zentao;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.DataLoader;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.IColumn;
import com.cnezsoft.zentao.data.TodoDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryDetailActivity extends ZentaoActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ENTRY_TYPE = "com.cnezsoft.zentao.ENTRY_TYPE";
    public static final String ARG_ID = "com.cnezsoft.zentao.ID";

    protected EntryType entryType = null;
    private long entryId = -1;
    protected boolean inherit = false;
    private DataEntry entry;
    private int layout;

    protected EntryType setEntryType() {
        return EntryType.Default;
    }

    public EntryDetailActivity() {
        entryType = setEntryType();
        inherit = entryType != null && entryType != EntryType.Default;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get argments from intent
        Intent intent = getIntent();
        if(intent == null) {
            throw new NullPointerException("Can't get intent object.");
        }
        String type = intent.getStringExtra(ARG_ENTRY_TYPE);
        if(type != null) {
            entryType = EntryType.valueOf(type);
        } else if(!inherit) {
            throw new NullPointerException("Can't get EntryType form intent object.");
        }
        long temp = intent.getLongExtra(ARG_ID, -1l);
        if(temp > -1) {
            entryId = temp;
        } else {
            throw new NullPointerException("Can't get ID form intent object.");
        }

        // choose layout
        switch (entryType) {
            default:
                layout = R.layout.activity_entry_detail;
                break;
        }

        setContentView(layout);

        // init loader
        getLoaderManager().initLoader(entryType.ordinal(), null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry_detail, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(String.format(getResources().getString(R.string.action_detail_format), entryType.text(this)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DataLoader.OnLoadDataListener listener = new DataLoader.OnLoadDataListener(){{}
            @Override
            public Cursor onLoadData(Context context) {
                TodoDAO dao = new TodoDAO(context);
                return dao.queryByKey(entryType, entryId + "");
            }
        };

        if(listener != null) {
            return new DataLoader(this, entryType, listener);
        }
        return null;
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p/>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link android.database.Cursor}
     * and you place it in a {@link android.widget.CursorAdapter}, use
     * the {@link android.widget.CursorAdapter#CursorAdapter(android.content.Context,
     * android.database.Cursor, int)} constructor <em>without</em> passing
     * in either {@link android.widget.CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link android.database.Cursor} from a {@link android.content.CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link android.widget.CursorAdapter}, you should use the
     * {@link android.widget.CursorAdapter#swapCursor(android.database.Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(entry == null) {
            entry = new DataEntry(entryType);
        }
        if(data.moveToNext()) {
            entry.fromCursor(data);
        }
        displayEntry();
    }

    private List<Map<String, Object>> getEntryData() {
        List<Map<String, Object>> list = new ArrayList<>();
        final Context context = this;
        for(final IColumn column: entryType.columns()) {
            list.add(new HashMap<String, Object>(){{
                put("text_name", column.text(context));
                put("text_value", entry.getFriendlyString(column));
            }});
        }

        return list;
    }

    private void displayEntry() {
        if(layout == R.layout.activity_entry_detail) {
            ListView listView = (ListView) findViewById(R.id.listView_default);
            SimpleAdapter adapter = (SimpleAdapter) listView.getAdapter();
            if(adapter == null) {
                adapter = new SimpleAdapter(this,
                        getEntryData(),
                        R.layout.list_item_entry_detail,
                        new String[]{"text_name", "text_value"},
                        new int[]{R.id.text_name, R.id.text_value});
            } else {
                adapter.notifyDataSetChanged();
            }
            listView.setAdapter(adapter);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        entry = null;
    }
}
