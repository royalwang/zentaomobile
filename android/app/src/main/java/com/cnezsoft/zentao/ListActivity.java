package com.cnezsoft.zentao;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;


public class ListActivity extends ActionBarActivity
        implements  NavigationDrawerFragment.ActivityWithDrawerMenu, TodoListFragment.OnFragmentInteractionListener
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private String currentNavItem = "todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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

        selectNavItem(currentNavItem);
//        actionBar.setElevation(0); // remove actions bar shadow
    }

    private void selectNavItem(String tag) {
        ActionBar actionBar = getSupportActionBar();
        LinearLayout navView = (LinearLayout) actionBar.getCustomView();
        for(int i = 0; i < navView.getChildCount(); ++i) {
            Button button = (Button) navView.getChildAt(i);
            if(button.getTag().equals(tag)) {
                button.setSelected(true);
                currentNavItem = tag;
                changeList(tag);
            } else {
                button.setSelected(false);
            }
        }
    }

    private void changeList(String tag) {
        TodoListFragment fragment = null;
        switch (tag) {
            case "todo":
                fragment = new TodoListFragment();
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
    public int getMenuId() {
        return NavigationDrawerFragment.STATE_LIST;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
