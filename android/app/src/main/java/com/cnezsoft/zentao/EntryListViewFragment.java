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
import android.widget.CursorAdapter;
import android.widget.IconTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.Bug;
import com.cnezsoft.zentao.data.BugColumn;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataLoader;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.IPageTab;
import com.cnezsoft.zentao.data.Story;
import com.cnezsoft.zentao.data.StoryColumn;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;

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

    private void initTodoAdapter() {
        final Activity activity = getActivity();
        adapter = new SimpleCursorAdapter(activity,
                R.layout.list_item_entry,
                null,
                new String[]{
                        TodoColumn.pri.name(),
                        TodoColumn.name.name(),
                        TodoColumn.unread.name(),
                        TodoColumn._id.name(),
                        TodoColumn.pri.name(),
                        TodoColumn.status.name()},
                new int[]{
                        R.id.icon,
                        R.id.text_title,
                        R.id.text_new_item,
                        R.id.text_id,
                        R.id.text_info,
                        R.id.text_status},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            private Todo todo;

            private String getCursorKey(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex(TodoColumn.primary().name()));
            }

            private Todo getTodo(Cursor cursor) {
                if(todo != null && todo.key().equals(getCursorKey(cursor))) {
                    return todo;
                } else {
                    todo = new Todo(cursor);
                }
                return todo;
            }

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.text_title:
                        getTodo(cursor);
                        TextView textView = (TextView) view;
//                        int pri = todo.getAccentPri();
//                        if(pri < 3) textView.setTextColor(MaterialColorSwatch.PriAccentSwatches[pri].primary().value());
//                        else textView.setTextColor(Color.BLACK);
                        textView.setText(todo.getFriendlyString(TodoColumn.name));
                        return true;
                    case R.id.icon:
                        getTodo(cursor);
                        IconTextView iconView = (IconTextView) view;
                        iconView.setTextColor(
                                MaterialColorSwatch.PriAccentSwatches[todo.getAccentPri()]
                                        .color(MaterialColorName.C300).value());
                        iconView.setText(todo.getStatus() == Todo.Status.done ? "{fa-check-circle-o}" : "{fa-circle-o}");
                        return true;
                    case R.id.text_id:
                        getTodo(cursor);
                        ((TextView) view).setText("#" + todo.getAsString(TodoColumn._id));
                        return true;
                    case R.id.text_info:
                        getTodo(cursor);
                        TextView statusView = (TextView) view;
                        Todo.Status status = todo.getStatus();
                        statusView.setText("{fa-" + status.icon() + "} " + ZentaoApplication.getEnumText(activity, status));
                        statusView.setTextColor(status.accent().primary().value());
                        if(status == Todo.Status.doing) {
                            statusView.setVisibility(View.VISIBLE);
                        } else {
                            statusView.setVisibility(View.GONE);
                        }
                        return true;
                    case R.id.text_new_item:
                        getTodo(cursor);
                        view.setVisibility(todo.isUnread() ? View.VISIBLE : View.GONE);
                        ((TextView) view).setText(String.format(getString(R.string.text_new_item_format), ZentaoApplication.getEnumText(activity, EntryType.Todo)));
                        return true;
                    case R.id.text_status:
                        getTodo(cursor);
                        ((TextView) view).setText(todo.getFriendlyTimeString(activity));
                        if(todo.isExpired() && todo.getStatus() != Todo.Status.done) ((TextView) view).setTextColor(MaterialColorSwatch.Red.primary().value());
                        else ((TextView) view).setTextColor(activity.getResources().getColor(R.color.secondary_text));
                        return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    private void initTaskAdapter() {
        final Activity activity = getActivity();
        adapter = new SimpleCursorAdapter(activity,
                R.layout.list_item_entry,
                null,
                new String[]{
                        TaskColumn.pri.name(),
                        TaskColumn.pri.name(),
                        TaskColumn.name.name(),
                        TaskColumn.unread.name(),
                        TaskColumn._id.name(),
                        TaskColumn.assignedTo.name(),
                        TaskColumn.status.name()},
                new int[]{
                        R.id.icon,
                        R.id.text_icon,
                        R.id.text_title,
                        R.id.text_new_item,
                        R.id.text_id,
                        R.id.text_info,
                        R.id.text_status},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            private Task entry;

            private String getCursorKey(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex(TaskColumn.primary().name()));
            }

            private Task getEntry(Cursor cursor) {
                if(entry != null && entry.key().equals(getCursorKey(cursor))) {
                    return entry;
                } else {
                    entry = new Task(cursor);
                }
                return entry;
            }

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.icon:
                        getEntry(cursor);
                        ((TextView) view).setTextColor(MaterialColorSwatch.PriAccentSwatches[entry.getAccentPri()]
                                .color(MaterialColorName.C300).value());
                        return true;
                    case R.id.text_icon:
                        getEntry(cursor);
                        int pri = entry.getAccentPri();
                        if(pri > 0) {
                            ((TextView) view).setText(pri + "");
                        }
                        return true;
                    case R.id.text_new_item:
                        getEntry(cursor);
                        view.setVisibility(entry.isUnread() ? View.VISIBLE : View.GONE);
                        if(view.getVisibility() == View.VISIBLE) {
                            ((TextView) view).setText(String.format(getString(R.string.text_new_item_format), ZentaoApplication.getEnumText(activity, EntryType.Task)));
                        }
                        return true;
                    case R.id.text_id:
                        getEntry(cursor);
                        ((TextView) view).setText("#" + entry.getAsString(TodoColumn._id));
                        return true;
                    case R.id.text_info:
                        getEntry(cursor);
                        String assignTo = entry.getFriendlyString(TaskColumn.assignedTo);
                        TextView infoView = (TextView) view;
                        if(Helper.isNullOrEmpty(assignTo)) {
                            infoView.setVisibility(View.INVISIBLE);
                        } else {
                            infoView.setVisibility(View.VISIBLE);
                            infoView.setText("{fa-hand-o-right} " + assignTo);
                        }
                        return true;
                    case R.id.text_status:
                        getEntry(cursor);
                        TextView statusView = (TextView) view;
                        Task.Status status = entry.getStatus();
                        statusView.setText(ZentaoApplication.getEnumText(activity, status));
                        statusView.setTextColor(status.accent().primary().value());
                        return true;

                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    private void initStoryAdapter() {
        final Activity activity = getActivity();
        adapter = new SimpleCursorAdapter(activity,
                R.layout.list_item_entry,
                null,
                new String[]{
                        StoryColumn.pri.name(),
                        StoryColumn.pri.name(),
                        StoryColumn.title.name(),
                        StoryColumn.unread.name(),
                        StoryColumn._id.name(),
                        StoryColumn.assignedTo.name(),
                        StoryColumn.status.name()},
                new int[]{
                        R.id.icon,
                        R.id.text_icon,
                        R.id.text_title,
                        R.id.text_new_item,
                        R.id.text_id,
                        R.id.text_info,
                        R.id.text_status},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            private Story entry;

            private String getCursorKey(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex(StoryColumn.primary().name()));
            }

            private Story getEntry(Cursor cursor) {
                if(entry != null && entry.key().equals(getCursorKey(cursor))) {
                    return entry;
                } else {
                    entry = new Story(cursor);
                }
                return entry;
            }

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.icon:
                        getEntry(cursor);
                        ((TextView) view).setTextColor(MaterialColorSwatch.PriAccentSwatches[entry.getAccentPri()]
                                .color(MaterialColorName.C300).value());
                        return true;
                    case R.id.text_icon:
                        getEntry(cursor);
                        int pri = entry.getAccentPri();
                        if(pri > 0) {
                            ((TextView) view).setText(pri + "");
                        }
                        return true;
                    case R.id.text_new_item:
                        getEntry(cursor);
                        view.setVisibility(entry.isUnread() ? View.VISIBLE : View.GONE);
                        if(view.getVisibility() == View.VISIBLE) {
                            ((TextView) view).setText(String.format(getString(R.string.text_new_item_format), ZentaoApplication.getEnumText(activity, EntryType.Task)));
                        }
                        return true;
                    case R.id.text_id:
                        getEntry(cursor);
                        ((TextView) view).setText("#" + entry.getAsString(TodoColumn._id));
                        return true;
                    case R.id.text_info:
                        getEntry(cursor);
                        String assignTo = entry.getFriendlyString(TaskColumn.assignedTo);
                        TextView infoView = (TextView) view;
                        if(Helper.isNullOrEmpty(assignTo)) {
                            infoView.setVisibility(View.INVISIBLE);
                        } else {
                            infoView.setVisibility(View.VISIBLE);
                            infoView.setText("{fa-hand-o-right} " + assignTo);
                        }
                        return true;
                    case R.id.text_status:
                        getEntry(cursor);
                        TextView statusView = (TextView) view;
                        Story.Status status = entry.getStatus();
                        statusView.setText(ZentaoApplication.getEnumText(activity, status));
                        statusView.setTextColor(status.accent().primary().value());
                        return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    private void initBugAdapter() {
        final Activity activity = getActivity();
        adapter = new SimpleCursorAdapter(activity,
                R.layout.list_item_entry,
                null,
                new String[]{
                        BugColumn.pri.name(),
                        BugColumn.pri.name(),
                        BugColumn.title.name(),
                        BugColumn.unread.name(),
                        BugColumn._id.name(),
                        BugColumn.assignedTo.name(),
                        BugColumn.confirmed.name(),
                        BugColumn.status.name()},
                new int[]{
                        R.id.icon,
                        R.id.text_icon,
                        R.id.text_title,
                        R.id.text_new_item,
                        R.id.text_id,
                        R.id.text_info,
                        R.id.text_extra_info,
                        R.id.text_status},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            private Bug entry;

            private String getCursorKey(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex(BugColumn.primary().name()));
            }

            private Bug getEntry(Cursor cursor) {
                if(entry != null && entry.key().equals(getCursorKey(cursor))) {
                    return entry;
                } else {
                    entry = new Bug(cursor);
                }
                return entry;
            }

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.icon:
                        getEntry(cursor);
                        ((TextView) view).setTextColor(MaterialColorSwatch.PriAccentSwatches[entry.getAccentPri()]
                                .color(MaterialColorName.C300).value());
                        return true;
                    case R.id.text_icon:
                        getEntry(cursor);
                        int pri = entry.getAccentPri();
                        if(pri > 0) {
                            ((TextView) view).setText(pri + "");
                        }
                        return true;
                    case R.id.text_new_item:
                        getEntry(cursor);
                        view.setVisibility(entry.isUnread() ? View.VISIBLE : View.GONE);
                        if(view.getVisibility() == View.VISIBLE) {
                            ((TextView) view).setText(String.format(getString(R.string.text_new_item_format), ZentaoApplication.getEnumText(activity, EntryType.Task)));
                        }
                        return true;
                    case R.id.text_id:
                        getEntry(cursor);
                        ((TextView) view).setText("#" + entry.getAsString(TodoColumn._id));
                        return true;
                    case R.id.text_info:
                        getEntry(cursor);
                        String assignTo = entry.getFriendlyString(TaskColumn.assignedTo);
                        TextView infoView = (TextView) view;
                        if(Helper.isNullOrEmpty(assignTo)) {
                            infoView.setVisibility(View.INVISIBLE);
                        } else {
                            infoView.setVisibility(View.VISIBLE);
                            infoView.setText("{fa-hand-o-right} " + assignTo);
                        }
                        return true;
                    case R.id.text_extra_info:
                        getEntry(cursor);
                        TextView confirmedView = (TextView) view;
                        if(!entry.getAsBoolean(BugColumn.confirmed)) {
                            confirmedView.setVisibility(View.VISIBLE);
                            confirmedView.setText("{fa-question-circle} " + getString(R.string.text_unconfirm));
                        }
                        return true;
                    case R.id.text_status:
                        getEntry(cursor);
                        TextView statusView = (TextView) view;
                        Bug.Status status = entry.getStatus();
                        statusView.setText(ZentaoApplication.getEnumText(activity, status));
                        statusView.setTextColor(status.accent().primary().value());
                        return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    private void initAdapter() {
        switch (entryType) {
            case Todo:
                initTodoAdapter();
                break;
            case Task:
                initTaskAdapter();
                break;
            case Bug:
                initBugAdapter();
                break;
            case Story:
                initStoryAdapter();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            entryType = EntryType.fromName(args.getString(EntryListFragment.ENTRY_TYPE));
            pageTab = entryType.getTab(args.getString(EntryListFragment.PAGE_TAB));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                return dao.query(pageTab, user.getAccount());
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
