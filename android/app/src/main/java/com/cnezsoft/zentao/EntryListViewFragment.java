package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataLoader;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.IPageTab;
import com.cnezsoft.zentao.data.TaskColumn;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Catouse on 2015/1/30.
 */
public class EntryListViewFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private IPageTab pageTab;
    private EntryType entryType;

    private SimpleCursorAdapter adapter;

    public static EntryListViewFragment newInstance(IPageTab page) {
        EntryListViewFragment fragment = new EntryListViewFragment();
        Bundle args = new Bundle();
        args.putString(EntryListFragment.ENTRY_TYPE, page.getEntryType().name());
        args.putString(EntryListFragment.PAGE_TAB, page.name());
        fragment.setArguments(args);
        return fragment;
    }

    public EntryListViewFragment() {
    }

    private void initAdapter() {
        Activity activity = getActivity();
        switch (entryType) {
            case Todo:
                adapter = new SimpleCursorAdapter(activity,
                        R.layout.list_item_todo,
                        null,
                        new String[]{TodoColumn.name.name(), TodoColumn.begin.name(), TodoColumn.status.name(), TodoColumn.pri.name()},
                        new int[]{R.id.text_todo, R.id.text_time, R.id.checkbox_todo, R.id.color_pri}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        switch (view.getId()) {
                            case R.id.text_time:
                                ((TextView) view).setText(new SimpleDateFormat("HH:mm").format(new Date(cursor.getLong(columnIndex))));
                                return true;
                            case R.id.checkbox_todo:
                                ((CheckBox) view).setChecked(cursor.getString(columnIndex).toUpperCase().equals(Todo.Status.done.name()));
                                return true;
                            case R.id.color_pri:
                                view.setBackgroundColor(MaterialColorSwatch.PriAccentSwatches[cursor.getInt(columnIndex)].color(MaterialColorName.A200).getColor());
                                return true;
                        }
                        return false;
                    }
                });
                break;
            case Task:
                adapter = new SimpleCursorAdapter(activity,
                        R.layout.list_item_task,
                        null,
                        new String[]{TaskColumn.name.name(), TaskColumn.status.name(), TaskColumn.pri.name()},
                        new int[]{R.id.text_name, R.id.text_status, R.id.icon_pri}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        switch (view.getId()) {
                            case R.id.text_status:

//                                ((TextView) view).setText(new SimpleDateFormat("HH:mm").format(new Date(cursor.getLong(columnIndex))));
                                return true;
                            case R.id.text_name:
                                ((TextView) view).setText(cursor.getString(columnIndex));
                                return true;
                            case R.id.icon_pri:

//                                view.setBackgroundColor(MaterialColorSwatch.PriAccentSwatches[cursor.getInt(columnIndex)].color(MaterialColorName.A200).getColor());
                                return true;
                        }
                        return false;
                    }
                });
                break;
        }

        setListAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            entryType = EntryType.fromName(args.getString(EntryListFragment.ENTRY_TYPE));
            pageTab = entryType.getTab(args.getString(EntryListFragment.PAGE_TAB));
        }

        initAdapter();
        getLoaderManager().initLoader(entryType.ordinal(), null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.v("LIST VIEW", "onListItemClick=" + position + ":" + id);
        Activity activity = getActivity();
        ((ZentaoApplication) activity.getApplicationContext()).openDetailActivity(activity, pageTab.getEntryType(), id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Activity activity = getActivity();
        final User user = ((ZentaoApplication) activity.getApplicationContext()).getUser();
        DataLoader.OnLoadDataListener listener = new DataLoader.OnLoadDataListener(){{}
            @Override
            public Cursor onLoadData(Context context) {
                DAO dao = new DAO(activity);
                return dao.query(pageTab, user);
            }
        };
        return new DataLoader(getActivity(), pageTab.getEntryType(), listener);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }
}
