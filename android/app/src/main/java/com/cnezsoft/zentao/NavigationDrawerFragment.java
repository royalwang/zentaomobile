package com.cnezsoft.zentao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private TextView textViewUserAccount;
    private TextView textViewUserAddress;
    private TextView textViewUserLastSyncTime;
    private Button buttonChangeUser;
    private Button buttonSyncNow;

    private AppNav currentNav = AppNav.home;
    private boolean mUserLearnedDrawer;
    private BroadcastReceiver syncReceiver = null;
    private TextView textViewSyncTitle;
    private CharSequence oldActionBarTitle;

    private Animation fadeOutAnimation;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        mUserLearnedDrawer = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);

        getAppNavFromActivity();

        // Select either the default item (0) or the last selected item.
        setStateSelectedPosition();

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        setUp();
        updateUserInfo();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerListView = (ListView) view.findViewById(R.id.navigation_drawer_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(AppNav.fromPosition(position));
            }
        });

        final AppNav[] navs = AppNav.values();
        final String[] drawerList = ZentaoApplication.getEnumTexts(getActivity(), AppNav.values());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                R.layout.list_item_navigation_drawer,
                R.id.text_menu_name,
                drawerList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_navigation_drawer, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.text_menu_name)).setText(drawerList[position]);
                ((TextView) convertView.findViewById(R.id.icon_menu)).setText("{fa-" + navs[position].getIcon() + "}");
                return convertView;
            }
        };
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setItemChecked(currentNav.getPosition(), true);

        // Store controls
        textViewUserAccount = ((TextView) view.findViewById(R.id.text_user_account));
        textViewUserAddress = ((TextView) view.findViewById(R.id.text_user_address));
        textViewUserLastSyncTime = ((TextView) view.findViewById(R.id.text_user_last_sync_time));
        buttonChangeUser = (Button) view.findViewById(R.id.button_change_user);
        buttonSyncNow = (Button) view.findViewById(R.id.button_sync_now);
        textViewSyncTitle = (TextView) view.findViewById(R.id.text_sync_title);

        // Bind event
        bindMenuEvents(view);

        return view;
    }

    @Override
    public void onPause() {
        if(syncReceiver != null) {
            getActivity().unregisterReceiver(syncReceiver);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Synchronizer.MESSAGE_OUT_SYNC);
        if(syncReceiver == null) {
            syncReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, Intent intent) {
                    if(intent.getAction().equals(Synchronizer.MESSAGE_OUT_SYNC)) {
                        final boolean syncResult = intent.getBooleanExtra("result", false);
                        updateUserInfo();
                        if(!buttonSyncNow.isEnabled()) {
                            fadeOutAnimation.setStartOffset(600);
                            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                 public void onAnimationEnd(Animation animation) {
                                    buttonSyncNow.clearAnimation();
                                    if(syncResult) {
                                        textViewSyncTitle.setText(context.getString(R.string.text_sync_success));
                                        buttonSyncNow.setText("{fa-check}");
                                        buttonSyncNow.setTextColor(MaterialColorSwatch.Green.primary().value());
                                    } else {
                                        textViewSyncTitle.setText(context.getString(R.string.text_sync_failure));
                                        buttonSyncNow.setText("{fa-exclamation-circle}");
                                        buttonSyncNow.setTextColor(MaterialColorSwatch.Red.primary().value());
                                    }

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Animation finalAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);
                                            finalAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {
                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    textViewSyncTitle.setText(context.getString(R.string.text_last_sync));
                                                    buttonSyncNow.setText("{fa-refresh}");
                                                    buttonSyncNow.setTextColor(context.getResources().getColor(R.color.primary));
                                                    buttonSyncNow.setEnabled(true);
                                                }
                                                @Override
                                                public void onAnimationRepeat(Animation animation) {
                                                }
                                            });
                                            buttonSyncNow.clearAnimation();
                                            buttonSyncNow.startAnimation(finalAnimation);
                                        }
                                    }, 2000);

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });

                            textViewSyncTitle.clearAnimation();
                            textViewSyncTitle.startAnimation(fadeOutAnimation);
                        }
                    }
                }
            };
        }

        getActivity().registerReceiver(syncReceiver, intentFilter);
    }

    /**
     * Called immediately after {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean getAppNavFromActivity() {
        try {
            AppNav nav = ((ActivityWithDrawerMenu) getActivity()).getAppNav();
            if(nav != currentNav) {
                currentNav = nav;
                return true;
            }
        } catch (ClassCastException e) {
            Log.w("Drawer", "Activity must implement ActivityWithDrawerMenu.");
        }
        return false;
    }

    private User.Status updateUserInfo() {
        User user = ((ZentaoApplication) getActivity().getApplicationContext()).getUser();
        User.Status status = user.getStatus();
        if(status != User.Status.UNKNOWN) {
            textViewUserAccount.setText(user.getName()
                    + (status == User.Status.OFFLINE ? (" [" + (getString(R.string.text_offline)) + "]") : ""));
            textViewUserAddress.setText(user.getAddress());
            textViewUserLastSyncTime.setText(user.getLastSyncTimeStr(getActivity()));
        } else {
            textViewUserAccount.setText(getText(R.string.message_please_login));
            textViewUserAddress.setText("...");
            textViewUserLastSyncTime.setText("...");
        }

        return status;
    }

    private void bindMenuEvents(View view) {
        buttonChangeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                ((ZentaoApplication) activity.getApplicationContext()).logout(activity);
            }
        });

        textViewUserAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.Status status = updateUserInfo();
                if(status != User.Status.ONLINE) {
                    Activity activity = getActivity();
                    ZentaoApplication application = ((ZentaoApplication) activity.getApplicationContext());
                    application.login(new CustomAsyncTask.OnPostExecuteHandler<OperateBundle<Boolean, User>>() {
                        @Override
                        public void onPostExecute(OperateBundle<Boolean, User> booleanUserOperateBundle) {
                            if(booleanUserOperateBundle.getResult()) {
                                updateUserInfo();
                            }
                        }
                    });
                }
            }
        });

        buttonSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = getActivity();
                Animation animation = AnimationUtils.loadAnimation(activity, R.anim.rotate_normal);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount((int) ((1000 * 15) / animation.getDuration()));
                buttonSyncNow.setEnabled(false);
                buttonSyncNow.setAnimation(animation);

                fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        textViewSyncTitle.setText(activity.getString(R.string.text_syncing));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                textViewSyncTitle.startAnimation(fadeOutAnimation);

                Intent intent = new Intent(Synchronizer.MESSAGE_IN_SYNC);
                getActivity().sendBroadcast(intent);
            }
        });
    }

    /**
     * Judge whether the drawer is opening
     * @return
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     */
    private void setUp() {
        int fragmentId = R.id.navigation_drawer;
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()

                handleDrawerOpened();
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void handleDrawerOpened() {
        updateUserInfo();
        if(getAppNavFromActivity()) {
            setStateSelectedPosition();
        }
    }

    private void setStateSelectedPosition() {
        setSelectNav(currentNav);
    }

    private void setSelectNav(AppNav nav) {
        int position = nav.getPosition();
        currentNav = nav;
        if(mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
    }

    private void selectItem(AppNav nav) {
        int position = nav.getPosition();
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

        Activity activity = getActivity();
        if(currentNav.getPosition() != position) {
            setSelectNav(nav);
            ((ZentaoApplication) activity.getApplicationContext()).openActivity(activity, nav);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            Log.w("Drawer", "Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentNav.getPosition());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            if(menu.findItem(R.id.action_settings) == null) {
                inflater.inflate(R.menu.global, menu);
            }
            showGlobalContextActionBar();
        } else {
            if(oldActionBarTitle == null) {
                oldActionBarTitle = getActionBar().getTitle();
            } else {
                getActionBar().setTitle(oldActionBarTitle);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayShowCustomEnabled(false);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    /**
     * Activity with drawer menu
     */
    public static interface ActivityWithDrawerMenu {
        AppNav getAppNav();
    }
}
