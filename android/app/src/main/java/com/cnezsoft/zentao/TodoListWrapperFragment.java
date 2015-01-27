package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
public class TodoListWrapperFragment extends ListFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PAGE = "page";
    public static final String ARG_GROUP = "group";
    public static final String ARG_ORDER = "order";

    private Todo.PageTab page;
    private Todo.Order order;
    private Todo.Group group;

    private OnFragmentInteractionListener mListener;

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
        TodoDAO dao = new TodoDAO(activity);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(activity,
                R.layout.list_item_todo,
                dao.query(page, order),
                new String[]{TodoColumn.name.name(), TodoColumn.begin.name(), TodoColumn.status.name(), TodoColumn.pri.name()},
                new int[]{R.id.text_todo, R.id.text_time, R.id.checkbox_todo, R.id.color_pri});

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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(id + "");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
