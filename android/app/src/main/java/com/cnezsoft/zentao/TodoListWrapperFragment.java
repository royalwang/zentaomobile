package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.data.DataLoader;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;
import com.cnezsoft.zentao.data.TodoDAO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TodoListWrapperFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_PAGE = "page";
    public static final String ARG_GROUP = "group";
    public static final String ARG_ORDER = "order";

    public static final int LOADER_TODO = 0;

    private Todo.PageTab page;
    private Todo.Order order;
    private Todo.Group group;

    private SimpleCursorAdapter adapter;

    public static TodoListWrapperFragment newInstance(Todo.PageTab page) {
        return newInstance(page, Todo.Order.pri, Todo.Group.time);
    }

    public static TodoListWrapperFragment newInstance(Todo.PageTab page, Todo.Order order, Todo.Group group) {
        TodoListWrapperFragment fragment = new TodoListWrapperFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, page.toString());
        args.putString(ARG_ORDER, order.toString());
        args.putString(ARG_GROUP, group.toString());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoListWrapperFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            page  = Todo.PageTab.valueOf(getArguments().getString(ARG_PAGE));
            order = Todo.Order.valueOf(getArguments().getString(ARG_ORDER));
            group = Todo.Group.valueOf(getArguments().getString(ARG_GROUP));
        }

        final Activity activity = getActivity();

        Log.v("TodoListWrapperFragment", "page: " + page.toString());
        Log.v("TodoListWrapperFragment", "order: " + order.toString());
        Log.v("TodoListWrapperFragment", "group: " + group.toString());

        adapter = new SimpleCursorAdapter(activity,
                R.layout.list_item_todo,
                null,
                new String[]{TodoColumn.name.name(), TodoColumn.begin.name(), TodoColumn.status.name(), TodoColumn.pri.name()},
                new int[]{R.id.text_todo, R.id.text_time, R.id.checkbox_todo, R.id.color_pri}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        final int[] priColors = activity.getResources().getIntArray(R.array.pri_colors);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.text_time:
                        ((TextView) view).setText(new SimpleDateFormat("HH:mm").format(new Date(cursor.getLong(columnIndex))));
                        return true;
                    case R.id.checkbox_todo:
                        ((CheckBox) view).setChecked(cursor.getString(columnIndex).toUpperCase().equals(Todo.Status.DONE.name()));
                        return true;
                    case R.id.color_pri:
                        view.setBackgroundColor(priColors[cursor.getInt(columnIndex)]);
                        return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);

        getLoaderManager().initLoader(LOADER_TODO, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.v("LIST VIEW", "onListItemClick=" + position + ":" + id);
        Activity activity = getActivity();
        ((ZentaoApplication) activity.getApplicationContext()).openDetailActivity(activity, EntryType.Todo, id);
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
        DataLoader.OnLoadDataListener listener = null;
        switch (id) {
            case LOADER_TODO:
                listener = new DataLoader.OnLoadDataListener(){{}
                    @Override
                    public Cursor onLoadData(Context context) {
                        TodoDAO dao = new TodoDAO(context);
                        return dao.query(page, order);
                    }
                };
                break;
        }
        if(listener != null) {
            return new DataLoader(getActivity(), EntryType.Todo, listener);
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
        adapter.changeCursor(data);
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
        adapter.changeCursor(null);
    }
}
