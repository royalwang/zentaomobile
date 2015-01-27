package com.cnezsoft.zentao;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.cnezsoft.zentao.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TodoListWrapperFragment extends ListFragment {

    /**
     * Page tabs
     */
    public enum PageTab {
        Wait,
        Done
    }

    /**
     * Group
     */
    public enum Group {
        None,
        Time,
        Pri,
        Type
    }

    /**
     * Order
     */
    public enum Order {
        Time,
        Pri,
        ID
    }


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PAGE = "page";
    public static final String ARG_GROUP = "group";
    public static final String ARG_ORDER = "order";

    private PageTab page;
    private Order order;
    private Group group;

    private OnFragmentInteractionListener mListener;

    public static TodoListWrapperFragment newInstance(PageTab page) {
        return newInstance(page, Order.Pri, Group.Time);
    }

    public static TodoListWrapperFragment newInstance(PageTab page, Order order, Group group) {
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
            page  = PageTab.valueOf(getArguments().getString(ARG_PAGE));
            order = Order.valueOf(getArguments().getString(ARG_ORDER));
            group = Group.valueOf(getArguments().getString(ARG_GROUP));
        }

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
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
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
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
