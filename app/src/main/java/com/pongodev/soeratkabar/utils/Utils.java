package com.pongodev.soeratkabar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.gms.ads.AdView;

/**
 * Created by pongodev on 5/27/15.
 */
public class Utils {
    public static final String EXTRA_ACTIVITY   = "activity";
    public static final String EXTRA_NEWS_ID    = "newsId";
    public static final String TAG_ACTIVITY_HOME= "activityHome";
    public static final String EXTRA_SOCIAL_URL = "socialUrl";
    public static final String EXTRA_NEWS_TITLE = "newsTitle";

    public static final String API_WORDPRESS    = "http://pongodev.com/?json=";

    // Wordpress Parameter
    public static final String PARAM_COUNT  = "count=";
    public static final String PARAM_SEARCH = "search=";

    public static final String PARAM_STATUS     = "status=published";
    public static final String PARAM_OFFSET     = "offset=";
    public static final String PARAM_POST_TYPE  = "post_type=";
    public static final String PARAM_CATEGORIES = "cat=";
    public static final String PARAM_POST_ID	= "post_id=";
    public static final String PARAM_PAGE       = "page=";

    // Key WORDPRESS
    public static final String KEY_ID               = "id";
    public static final String KEY_TITLE            = "title";
    public static final String KEY_EXCERPT          = "excerpt";
    public static final String KEY_DATE             = "date";
    public static final String KEY_URL              = "url";
    public static final String KEY_IMAGE_FULL_URL   = "urlFull";
    public static final String KEY_IMAGE_THUMBNAIL_URL = "urlThumb";
    public static final String KEY_PAGES            = "pages";
    public static final String KEY_STATUS           = "status";
    public static final String KEY_NEWS_AUTHOR		= "author";
    public static final String KEY_NEWS_AUTHOR_NAME	= "name";
    public static final String KEY_NEWS_IMAGE		= "images";
    public static final String KEY_NEWS_FULL_THUMB	= "full";
    public static final String KEY_NEWS_URL		    = "url";

    public static final int VALUE_PER_PAGE   	    = 6;
    public static final String VALUE_DEFAULT        ="blank";
    public static final String VALUE_GET_POSTS   	= "get_posts";
    public static final String VALUE_GET_SEARCH_RESULTS = "get_search_results";
    public static final String VALUE_POST   	    = "post";
    public static final String VALUE_GET_POST       = "get_post";
    public static final String VALUE_JSON_CATEGORY  = "get_category_index";


    // Array Wordpress
    public static final String ARRAY_POSTS       = "posts";
    public static final String ARRAY_ATTACHMENTS = "attachments";
    public static final String ARRAY_CATEGORY    = "categories";



    // Object WORDPRESS
    public static final String OBJECT_IMAGES        = "images";
    public static final String OBJECT_IMAGES_FULL   = "full";
    public static final String OBJECT_IMAGES_THUMBNAIL = "thumbnail";
    public static final String OBJECT_NEWS_POST		= "post";

    // Category data fields
    public static final String EXTRA_CATEGORY_ID   	= "category_id";
    
    // Default is 2500
    public static final Integer ARG_TIMEOUT_MS  = 4000;

    // Admob visibility parameter. Set true to show admob and false to hide.
    public static final boolean IS_ADMOB_VISIBLE = false;

    // Set value to true if you are still in development process, and false if you are ready to publish the app.
    public static final boolean IS_ADMOB_IN_DEBUG = false;

    // Debugging tag for the application
    public static final String TAG_PONGODEV  = "Pongodev:";
    public static final String TAG_ACTIVITY_NEWSBYCATEGORY="newsByCategory";

    // Preference key, do NOT change this
    public static final String ARG_DEFAULT_PREFERENCE = "unknown";
    public static final String ARG_DRAWER_PREFERENCE  = "drawerPreference";
    public static final String ARG_ADMOB_PREFERENCE   = "admobPreference";
    public static final String ARG_PREFERENCES_DRAWER = "viewDrawerPreference";

    public static final String timeoutMessageHtml = "<html><body><p>Error loading url: No Connection or connection down</p></body></html>";


    // Method to check admob visibility
    public static boolean admobVisibility(AdView ad, boolean isInDebugMode){
        if(isInDebugMode) {
            ad.setVisibility(View.VISIBLE);
            return true;
        }else {
            ad.setVisibility(View.GONE);
            return false;
        }
    }

    // Method to save integer value to SharedPreferences
    public static void saveIntPreferences(Context ctx, String key, String param, int value){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(param, value);
        editor.apply();
    }

    // Method to save string value to SharedPreferences
    public static void saveStringPreferences(Context ctx, String key, String param, String value){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(param, value);
        editor.apply();
    }

    // Method to load int value from SharedPreferences
    public static int loadIntPreferences(Context ctx, String key, String param){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        if(key.equals(ARG_ADMOB_PREFERENCE)){
            return sharedPreferences.getInt(param, 1);
        }else {
            return sharedPreferences.getInt(param, 1000);
        }
    }

}
