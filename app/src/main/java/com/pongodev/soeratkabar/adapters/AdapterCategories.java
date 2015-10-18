package com.pongodev.soeratkabar.adapters;

/**
 * Created by cahaya on 10/10/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.pongodev.soeratkabar.R;
import com.pongodev.soeratkabar.libs.MySingleton;
import com.pongodev.soeratkabar.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Design and developed by pongodev.com
 *
 * AdapterCategories is created to category item.
 * Created using UltimateViewAdapter.
 */
public class AdapterCategories extends UltimateViewAdapter<RecyclerView.ViewHolder> {

    private final ArrayList<HashMap<String,String>> DATA;

    public AdapterCategories(Context context, ArrayList<HashMap<String, String>> list){
        ImageLoader IMAGE_LOADER = MySingleton.getInstance(context).getImageLoader();
        DATA=list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_categories, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        HashMap<String, String> item;
        item = DATA.get(customHeaderView != null ? position - 1 : position);
        // Set category name to textview
        ((ViewHolder) holder).txtCategoryName.setText(item.get(Utils.KEY_TITLE));

    }

    @Override
    public int getAdapterItemCount() {
        return DATA.size();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new UltimateRecyclerviewViewHolder(view);
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }

    public static class ViewHolder extends UltimateRecyclerviewViewHolder {
        // Create view objects
        final TextView txtCategoryName;

        public ViewHolder(View v) {
            super(v);
            // Connect view objects with view ids in xml
            txtCategoryName = (TextView) v.findViewById(R.id.txtCategoryName);
        }
    }
}
