package com.pongodev.soeratkabar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.pongodev.soeratkabar.R;
import com.pongodev.soeratkabar.adapters.AdapterList;
import com.pongodev.soeratkabar.fragments.FragmentCategories;
import com.pongodev.soeratkabar.fragments.FragmentFinds;
import com.pongodev.soeratkabar.fragments.FragmentNews;
import com.pongodev.soeratkabar.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityHome extends AppCompatActivity implements
         NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = ActivityHome.class.getSimpleName();

    private TextView mLblNoResult;
    private LinearLayout mLytRetry;
    //private CircleProgressBar mPrgLoading;
    private AdapterList mAdapter;
    private UltimateRecyclerView mUltimateRecyclerView;

    // Paramater (true = data still exist in server, false = data already loaded all)
    private boolean mIsStillLoding = true;
    // Paramater (true = is first time, false = not first time)
    private boolean mIsAppFirstLaunched = true;

    private String mCategoryId, mActivity;

    // Return media earlier than this max_id.
    private Integer mCurrentPage=1;

    private ArrayList<HashMap<String, String>> newsData = new ArrayList<>();

    private boolean mIsAdmobVisible;

    private int mStartIndex=0;

    private Toolbar toolbar;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Connect view objects and view ids from xml
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // mPrgLoading           = (CircleProgressBar) findViewById(R.id.prgLoading);
        setSupportActionBar(toolbar);
        //mPrgLoading.setColorSchemeResources(R.color.accent_color);
       // mPrgLoading.setVisibility(View.VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* In case this activity was started with special instructions from an Intent,
        Pass the Intent's extras to the fragment as arguments */

        // Display FragmentNews by default
        Bundle bundle = new Bundle();
        bundle.putString(Utils.EXTRA_ACTIVITY, Utils.TAG_ACTIVITY_HOME);
        //bundle.putString(Utils.TAG_PARENT_ACTIVITY, Utils.TAG_ACTIVITY_HOME);
        mFragment = new FragmentNews();
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mFragment)
                .commit();

    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Utils.PARAM_PREFERENCES_DRAWER) != id
        // it means menu can't click again when in there
        Log.e("testing","= "+Utils.loadIntPreferences(getApplicationContext(),
                Utils.ARG_DRAWER_PREFERENCE,
                Utils.ARG_PREFERENCES_DRAWER));

        if (id == R.id.nav_latest_news &&
                Utils.loadIntPreferences(getApplicationContext(),
                        Utils.ARG_DRAWER_PREFERENCE,
                        Utils.ARG_PREFERENCES_DRAWER) !=
                        R.id.nav_latest_news) {

            // Set toolbar title and selected drawer item
            toolbar.setTitle(getString(R.string.latest_news));
            Bundle bundle = new Bundle();
            bundle.putString(Utils.EXTRA_ACTIVITY, Utils.TAG_ACTIVITY_HOME);

            FragmentManager mFragmentManager = getSupportFragmentManager();
            mFragmentManager.popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // Create FragmentPlaces object
            mFragment = new FragmentNews();
            //mFragment.setArguments(bundle);

            // Replace fragment in fragment_container with FragmentPlaces
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mFragment)
                    .commit();

        } else if (id == R.id.nav_category &&
                Utils.loadIntPreferences(getApplicationContext(),
                        Utils.ARG_DRAWER_PREFERENCE,
                        Utils.ARG_PREFERENCES_DRAWER) !=
                        id) {

            // Set toolbar title and selected drawer item
            toolbar.setTitle(getString(R.string.categories));

            // Create FragmentCategories object
            mFragment = new FragmentCategories();

            // Replace fragment in fragment_container with FragmentCategories
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_find &&
                Utils.loadIntPreferences(getApplicationContext(),
                        Utils.ARG_DRAWER_PREFERENCE,
                        Utils.ARG_PREFERENCES_DRAWER) !=
                        id) {

            // Set toolbar title and selected drawer item
            toolbar.setTitle(getString(R.string.find));

            // Create FragmentCategories object
            mFragment = new FragmentFinds();

            // Replace fragment in fragment_container with FragmentCategories
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_facebook &&
                Utils.loadIntPreferences(getApplicationContext(),
                        Utils.ARG_DRAWER_PREFERENCE,
                        Utils.ARG_PREFERENCES_DRAWER) !=
                        id) {

            Intent i = new Intent(this, ActivityBrowser.class);
            i.putExtra(Utils.EXTRA_SOCIAL_URL, getString(R.string.url_facebook));
            i.putExtra(Utils.EXTRA_NEWS_TITLE, getString(R.string.facebook_title));
            startActivity(i);
            overridePendingTransition(R.anim.open_next, R.anim.close_main);


        } else if (id == R.id.nav_twitter &&
                Utils.loadIntPreferences(getApplicationContext(),
                        Utils.ARG_DRAWER_PREFERENCE,
                        Utils.ARG_PREFERENCES_DRAWER) !=
                        id) {

            Intent i = new Intent(this, ActivityBrowser.class);
            i.putExtra(Utils.EXTRA_SOCIAL_URL, getString(R.string.url_twitter));
            i.putExtra(Utils.EXTRA_NEWS_TITLE, getString(R.string.twitter_title));
            startActivity(i);
            overridePendingTransition(R.anim.open_next, R.anim.close_main);

        } else if (id == R.id.nav_about &&
                Utils.loadIntPreferences(getApplicationContext(),
                        Utils.ARG_DRAWER_PREFERENCE,
                        Utils.ARG_PREFERENCES_DRAWER) !=
                        id) {

            // Open setting page by calling ActivitySettings.java
            Intent settingsIntent = new Intent(getApplicationContext(),
                    ActivityAbout.class);
            startActivity(settingsIntent);
            overridePendingTransition(R.anim.open_next, R.anim.close_main);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
