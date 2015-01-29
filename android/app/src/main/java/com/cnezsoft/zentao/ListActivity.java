package com.cnezsoft.zentao;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cnezsoft.zentao.data.Todo;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;


public class ListActivity extends ZentaoActivity
        implements  NavigationDrawerFragment.ActivityWithDrawerMenu, TodoListFragment.OnFragmentInteractionListener
{
    public static final String NAV_CURRENT = "NAV_CURRENT";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private DashboardNav currentNav = DashboardNav.todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        currentNav = DashboardNav.values()[getIntent().getIntExtra(NAV_CURRENT, 0)];

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
    }

    /**
     * Restore action bar
     */
    public void restoreActionBar(Menu menu) {
        // set icon to menu items
        menu.getItem(0).setIcon(new IconDrawable(this, Iconify.IconValue.fa_search){{
            sizeRes(R.dimen.action_bar_icon_size); colorRes(R.color.icons);}});

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.list_navigation);

        LinearLayout navView = (LinearLayout) actionBar.getCustomView();
        for(int i = 0; i < navView.getChildCount(); ++i) {
            navView.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectNavItem(v.getTag().toString());
                }
            });
        }

        selectNavItem(currentNav);
    }

    private void selectNavItem(String name) {
        selectNavItem(DashboardNav.valueOf(name.toLowerCase()));
    }

    private void selectNavItem(DashboardNav nav) {
        String tag = nav.name();
        ActionBar actionBar = getSupportActionBar();
        LinearLayout navView = (LinearLayout) actionBar.getCustomView();
        for(int i = 0; i < navView.getChildCount(); ++i) {
            Button button = (Button) navView.getChildAt(i);
            if(button.getTag().equals(tag)) {
                button.setSelected(true);
                currentNav = nav;
                changeList(tag);
            } else {
                button.setSelected(false);
            }
        }
    }

    private void changeList(String tag) {
        android.app.Fragment fragment = null;
        switch (tag) {
            case "todo":
                fragment = TodoListFragment.newInstance(Todo.PageTab.wait);
                break;
        }

        if(fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.list_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_list, menu);
            restoreActionBar(menu);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public AppNav getAppNav() {
        return AppNav.valueOf(currentNav.name());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
