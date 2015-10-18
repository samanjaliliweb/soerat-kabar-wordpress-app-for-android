package com.pongodev.soeratkabar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.animators.internal.ViewHelper;
import com.pongodev.soeratkabar.R;
import com.pongodev.soeratkabar.libs.MySingleton;
import com.pongodev.soeratkabar.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Design and developed by pongodev.com
 *
 * AdapterLocations2 is created to place list item
 */
public class AdapterList extends UltimateViewAdapter<RecyclerView.ViewHolder> {

    private final ArrayList<HashMap<String,String>> DATA;
    private int mLastPosition = 5;
    private final ImageLoader IMAGE_LOADER;

    public AdapterList(Context context, ArrayList<HashMap<String, String>> list){
        IMAGE_LOADER = MySingleton.getInstance(context).getImageLoader();
        DATA=list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position < getItemCount() && (customHeaderView != null ? position <= DATA.size() : position < DATA.size()) && (customHeaderView == null || position > 0)) {
            HashMap<String, String> item;
            item = DATA.get(customHeaderView != null ? position - 1 : position);
            ((ViewHolder) holder).lblTitle.setText(item.get(Utils.KEY_TITLE));
            ((ViewHolder) holder).lblDate.setText(item.get(Utils.KEY_DATE));

            if(!item.get(Utils.KEY_IMAGE_THUMBNAIL_URL).equals("empty")){
                IMAGE_LOADER.get(item.get(Utils.KEY_IMAGE_THUMBNAIL_URL),
                        ImageLoader.getImageListener(((ViewHolder) holder).imgThumbnail, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
            } else {
                ((ViewHolder) holder).imgThumbnail.setImageResource(R.mipmap.ic_launcher);
            }

        }

        ViewHelper.clear(holder.itemView);
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
        final TextView lblTitle, lblDate;
        final ImageView imgThumbnail;

        public ViewHolder(View v) {
            super(v);
            lblTitle     = (TextView) v.findViewById(R.id.lblTitle);
            lblDate     = (TextView) v.findViewById(R.id.lblDate);
            imgThumbnail = (ImageView) v.findViewById(R.id.imgThumbnail);
        }
    }

}
