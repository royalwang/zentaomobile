package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.cnezsoft.zentao.data.Todo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoListFragment extends Fragment {

    private Todo.PageTab page;
    private Todo.Order order;
    private Todo.Group group;

    private OnFragmentInteractionListener mListener;

    public static TodoListFragment newInstance(Todo.PageTab page) {
        return newInstance(page, Todo.Order._id, Todo.Group.time);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Todo_List_Fragment.
     */
    public static TodoListFragment newInstance(Todo.PageTab page, Todo.Order order, Todo.Group group) {
        TodoListFragment fragment = new TodoListFragment();
        Bundle args = new Bundle();
        args.putString(TodoListWrapperFragment.ARG_PAGE, page.toString());
        args.putString(TodoListWrapperFragment.ARG_ORDER, order.toString());
        args.putString(TodoListWrapperFragment.ARG_GROUP, group.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public TodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page  = Todo.PageTab.valueOf(getArguments().getString(TodoListWrapperFragment.ARG_PAGE)) ;
            order = Todo.Order.valueOf(getArguments().getString(TodoListWrapperFragment.ARG_ORDER)) ;
            group = Todo.Group.valueOf(getArguments().getString(TodoListWrapperFragment.ARG_GROUP)) ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_todo_list, container, false);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager_list);
        FragmentManager fragmentManager = getChildFragmentManager();
        pager.setAdapter(new TodoFragmentPagerAdapter(getActivity(), fragmentManager, order, group));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs_nav);
        tabs.setIndicatorColorResource(R.color.primary);
        tabs.setIndicatorHeight(6);
        tabs.setViewPager(pager);

        // Bind spinners
//        Spinner orderSpinner = (Spinner) view.findViewById(R.id.spinner_order);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.todo_list_orders));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        orderSpinner.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onFragmentInteraction(Uri uri);
    }
}
