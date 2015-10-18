package com.pongodev.soeratkabar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.pongodev.soeratkabar.R;
import com.pongodev.soeratkabar.fragments.FragmentNews;
import com.pongodev.soeratkabar.utils.Utils;

/**
 * Design and developed by pongodev.com
 *
 * FragmentCategory is created to display places by selected
 * category in recyclerview. Created using AppCompatActivity.
 */
public class ActivityPlaceByCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_app_bar_main);

        // Pass selected category from previous page
        // and replace the following toolbar title with selected category
        Intent i = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.EXTRA_CATEGORY_ID, i.getStringExtra(Utils.EXTRA_CATEGORY_ID));
        bundle.putString(Utils.EXTRA_ACTIVITY, Utils.TAG_ACTIVITY_NEWSBYCATEGORY);

        // Create FragmentPlaces object and replace fragment_container with it
        Fragment fragment = new FragmentNews();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
