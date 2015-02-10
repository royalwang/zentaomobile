package com.cnezsoft.zentao;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.IPageTab;

/**
 * Created by Catouse on 2015/1/30.
 */
public class EntryListFragment extends Fragment {

    public static final String PAGE_TAB = "com.cnezsoft.zentao.data.PageTab";
    public static final String ENTRY_TYPE = "com.cnezsoft.zentao.data.EntryType";

    private EntryType entryType;
    private IPageTab page;

//    private HashMap<String, IListFilter> listFilters = new HashMap<>();
//
//    protected void addListFilter(IListFilter filter) {
//        String name = filter.getClass().getSimpleName();
//        listFilters.put(name, filter);
//    }
//
//    public IListFilter getListFilter(String name) {
//        return listFilters.get(name);
//    }
//
//    public IListFilter getListFilter(Class<IListFilter> c) {
//        return listFilters.get(c.getSimpleName());
//    }

    public static EntryListFragment newInstance(IPageTab page) {
        EntryListFragment fragment = new EntryListFragment();
        Bundle args = new Bundle();
        args.putString(ENTRY_TYPE, page.getEntryType().name());
        args.putString(PAGE_TAB, page.name());
        fragment.setArguments(args);
        return fragment;
    }

    public EntryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            entryType = EntryType.fromName(args.getString(ENTRY_TYPE));
            page = entryType.getTab(args.getString(PAGE_TAB));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_entry_list, container, false);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager_list);
        FragmentManager fragmentManager = getChildFragmentManager();
        pager.setAdapter(new EntryListFragmentPagerAdapter(getActivity(), fragmentManager, page));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs_nav);
        tabs.setIndicatorColorResource(R.color.primary);
        tabs.setTextColorResource(R.color.secondary_text);
        tabs.setIndicatorHeight(6);
        tabs.setViewPager(pager);

        return view;
    }


}
