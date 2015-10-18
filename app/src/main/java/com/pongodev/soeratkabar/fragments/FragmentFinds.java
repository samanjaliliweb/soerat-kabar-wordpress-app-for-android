package com.pongodev.soeratkabar.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.pongodev.soeratkabar.R;
import com.pongodev.soeratkabar.activities.ActivityDetail;
import com.pongodev.soeratkabar.adapters.AdapterList;
import com.pongodev.soeratkabar.libs.MySingleton;
import com.pongodev.soeratkabar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Design and developed by pongodev.com
 *
 * FragmentNews is created to display latest location data.
 * Created using Fragment.
 */
public class FragmentFinds extends Fragment implements
        OnClickListener, TextView.OnEditorActionListener {

    // Tag for log
    private static final String TAG = FragmentFinds.class.getSimpleName();

    // Create view objects
    private TextView mLblNoResult;
    private EditText mTxtSearchFilter;
    private CircleProgressBar mPrgLoading;
    private LinearLayout mLytRetry;
    private UltimateRecyclerView mUltimateRecyclerView;

    // Create variable to handle search filter
    private String mKeyword = "";

    // Create adapter object
    private AdapterList mAdapter;

    // Paramater (true = data still exist in server, false = data already loaded all)
    private boolean mIsStillLoding = true;
    // Paramater (true = is first time, false = not first time)
    private boolean mIsAppFirstLaunched = true;

    // Create arraylist to store location data
    private ArrayList<HashMap<String, String>> newsData = new ArrayList<>();
    // Create variable to handle admob visibility
    private boolean mIsAdmobVisible;

    // Return media earlier than this max_id.
    private Integer mCurrentPage=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        Utils.saveIntPreferences(getActivity(), Utils.ARG_DRAWER_PREFERENCE,
                Utils.ARG_PREFERENCES_DRAWER, R.id.nav_find);

        // Connect view objects and view ids from xml
        mTxtSearchFilter      = (EditText) view.findViewById(R.id.txtSearchFilter);
        mUltimateRecyclerView = (UltimateRecyclerView) view.findViewById(R.id.recycler_view);
        mLblNoResult	      = (TextView) view.findViewById(R.id.lblNoResult);
        mLytRetry             = (LinearLayout) view.findViewById(R.id.lytRetry);
        mPrgLoading           = (CircleProgressBar) view.findViewById(R.id.prgLoading);

        AppCompatButton btnRetry    = (AppCompatButton) view.findViewById(R.id.btnRetry);
        AdView mAdView              = (AdView) view.findViewById(R.id.adView);
        // Set click listener to the button
        btnRetry.setOnClickListener(this);
        // Set circular progress bar color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);
        // Set editor action listener to the edittext
        mTxtSearchFilter.setOnEditorActionListener(this);
        // Set image loader object

        // Get admob visibility value
        mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);
        //Load ad in background using asynctask class
        new SyncShowAd(mAdView).execute();


        // Set arraylist
        newsData = new ArrayList<>();

        // Set mAdapter
        mAdapter = new AdapterList(getActivity(), newsData);
        mUltimateRecyclerView.setAdapter(mAdapter);
        mUltimateRecyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mUltimateRecyclerView.setLayoutManager(linearLayoutManager);

        mUltimateRecyclerView.enableLoadmore();
        // Set layout for custom loading when load more
        mAdapter.setCustomLoadMoreView(LayoutInflater.from(getActivity())
                .inflate(R.layout.loadmore_progressbar, null));

        // Listener for handle load more
        mUltimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                // if True is means still have data in server
                if (mIsStillLoding) {
                    // Set layout for custom the loading when load more. mAdapter is set
                    // again because when load data is response error setCustomLoadMoreView
                    // is null to clear view loading
                    mAdapter.setCustomLoadMoreView(LayoutInflater.from(getActivity())
                            .inflate(R.layout.loadmore_progressbar, null));

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            getNewsData(mKeyword);
                        }
                    }, 1000);
                } else {
                    disableLoadmore();
                }

            }
        });

        // Condition when item in list is click
        ItemTouchListenerAdapter itemTouchListenerAdapter = new ItemTouchListenerAdapter(mUltimateRecyclerView.mRecyclerView,
            new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView parent, View clickedView, int position) {
                    // To handle when position  = newsData.size means loading view si click
                    if (position < newsData.size()) {
                        Intent i = new Intent(getActivity(), ActivityDetail.class);
                        i.putExtra(Utils.EXTRA_NEWS_ID, newsData.get(position).get(Utils.KEY_ID));
                        startActivity(i);
                    }
                }

                @Override
                public void onItemLongClick(RecyclerView recyclerView, View view, int i) {
                }
            });

        // Enable touch listener
        mUltimateRecyclerView.mRecyclerView.addOnItemTouchListener(itemTouchListenerAdapter);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        InputMethodManager imm;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mKeyword = mTxtSearchFilter.getText().toString().trim();
            if(mKeyword.isEmpty() || mKeyword.equals("")){
                imm = (InputMethodManager) getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(mTxtSearchFilter.getWindowToken(), 0);

                new SnackBar.Builder(getActivity())
                        .withMessage(getString(R.string.type_place_name_first))
                        .show();

                // Have keyword
            } else {
                beginningView();

                imm = (InputMethodManager) getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(mTxtSearchFilter.getWindowToken(), 0);

                getNewsData(mKeyword);
            }

            handled = true;

        }
        return handled;
    }

    // Asynctask class to load admob in background
    public class SyncShowAd extends AsyncTask<Void, Void, Void> {

        final AdView ad;
        AdRequest adRequest;

        public SyncShowAd(AdView ad) {
            this.ad = ad;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check AD visibility. If visible, create adRequest
            if (mIsAdmobVisible) {
                // Create an AD request
                if (Utils.IS_ADMOB_IN_DEBUG) {
                    adRequest = new AdRequest.Builder().
                            addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Check ad visibility. If visible, display ad banner and interstitial
            if (mIsAdmobVisible) {
                // Start loading the ad
                ad.loadAd(adRequest);

            }

        }
    }

    private void getNewsData(String searchFilter) {
        // http://pongodev.com/?json=get_search_results&search=a&post_type=post&count=7&page=1

        String url = Utils.API_WORDPRESS+Utils.VALUE_GET_SEARCH_RESULTS+"&"+
                Utils.PARAM_SEARCH+searchFilter+"&"+
                Utils.PARAM_POST_TYPE+Utils.VALUE_POST+"&"+
                Utils.PARAM_COUNT+Utils.VALUE_PER_PAGE+"&"+
                Utils.PARAM_PAGE+mCurrentPage;

        Log.e("testing","url= "+url);
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    JSONArray postsWordpressArray, attachmentsWordpressArray;
                    JSONObject postsWordpressObject, attachmentsWordpressObject,
                            attachmentsImagesWordpressObject, attachmentsImagesFullWordpressObject;
                    Integer mTotalPages;

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get all json from server
                            postsWordpressArray = response.getJSONArray(Utils.ARRAY_POSTS);
                            if (postsWordpressArray.length() > 0) {
                                haveResultView();
                                for (int i = 0; i < postsWordpressArray.length(); i++) {
                                    HashMap<String, String> dataMap = new HashMap<>();
                                    postsWordpressObject = postsWordpressArray.getJSONObject(i);

                                    // Get id, title, and date
                                    dataMap.put(Utils.KEY_ID, postsWordpressObject.getString(Utils.KEY_ID));
                                    dataMap.put(Utils.KEY_TITLE, postsWordpressObject.getString(Utils.KEY_TITLE));
                                    dataMap.put(Utils.KEY_DATE, postsWordpressObject.getString(Utils.KEY_DATE));

                                    // Get Images Full size
                                    attachmentsWordpressArray = postsWordpressObject.getJSONArray(Utils.ARRAY_ATTACHMENTS);

                                    // Variable to store length of attach array
                                    int attachLength = attachmentsWordpressArray.length();

                                    // Condition if length of attach array 0
                                    if(attachLength==0){
                                        dataMap.put(Utils.KEY_IMAGE_FULL_URL, "empty");
                                        dataMap.put(Utils.KEY_IMAGE_THUMBNAIL_URL, "empty");
                                    } else {
                                        attachmentsWordpressObject = attachmentsWordpressArray.getJSONObject(0);
                                        attachmentsImagesWordpressObject = attachmentsWordpressObject.getJSONObject(Utils.OBJECT_IMAGES);
                                        attachmentsImagesFullWordpressObject = attachmentsImagesWordpressObject.getJSONObject(Utils.OBJECT_IMAGES_FULL);
                                        dataMap.put(Utils.KEY_IMAGE_FULL_URL, attachmentsImagesFullWordpressObject.getString(Utils.KEY_URL));

                                        // Get Images Thumbnail size
                                        attachmentsImagesFullWordpressObject = attachmentsImagesWordpressObject.getJSONObject(Utils.OBJECT_IMAGES_THUMBNAIL);
                                        dataMap.put(Utils.KEY_IMAGE_THUMBNAIL_URL, attachmentsImagesFullWordpressObject.getString(Utils.KEY_URL));
                                    }


                                    newsData.add(dataMap);

                                    // Insert 1 by 1 to mAdapter
                                    mAdapter.notifyItemInserted(newsData.size());
                                }
                                mTotalPages = response.getInt(Utils.KEY_PAGES);
                                // Condition when paginationFLICKRObject is null its mean nomore data in server.
                                if(mCurrentPage < mTotalPages){
                                    mCurrentPage+=1;
                                } else {
                                    disableLoadmore();

                                }

                                // If success get data it means next its not first time again
                                mIsAppFirstLaunched = false;

                                // Possibility still exist in server
                                mIsStillLoding = true;

                                // Data from server already load all or no data in server
                            } else {
                                if (mIsAppFirstLaunched && mAdapter.getAdapterItemCount() <= 0) {
                                    noResultView();
                                }
                                disableLoadmore();
                            }

                        } catch (JSONException e) {
                            Log.d(Utils.TAG_PONGODEV + TAG, "JSON Parsing error: " + e.getMessage());
                            mPrgLoading.setVisibility(View.GONE);
                        }
                        mPrgLoading.setVisibility(View.GONE);

                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // To make sure Activity is still in the foreground
                        Activity activity = getActivity();
                        if(activity != null && isAdded()){
                            Log.d(Utils.TAG_PONGODEV + TAG, "on Error Response: " + error.getMessage());
                            // "try-catch" To handle when still in process and then application closed
                            try {
                                if (error instanceof NoConnectionError) {
                                    retryView(getString(R.string.no_internet_connection));
                                } else {
                                    retryView(getString(R.string.response_error));
                                }

                                // To handle when no data in mAdapter and then get error because
                                // no connection or problem in server
                                if (newsData.size() == 0) {
                                    retryView(getString(R.string.no_result));

                                    // Conditon when loadmore, it have data when loadmore then get
                                    // error because no connection
                                } else {
                                    mAdapter.setCustomLoadMoreView(null);
                                    mAdapter.notifyDataSetChanged();
                                }

                                mPrgLoading.setVisibility(View.GONE);

                            } catch (Exception e) {
                                Log.d(Utils.TAG_PONGODEV + TAG, "failed catch volley " + e.toString());
                                mPrgLoading.setVisibility(View.GONE);
                            }
                        }

                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(Utils.ARG_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).getRequestQueue().add(request);

    }

    // Method to display retry layout if can not connect to the server
    private void retryView(String message) {
        mLytRetry.setVisibility(View.VISIBLE);
        mUltimateRecyclerView.setVisibility(View.GONE);
        mLblNoResult.setVisibility(View.GONE);
        mLblNoResult.setText(message);
    }

    // Method to display result view if data is available
    private void haveResultView() {
        mLytRetry.setVisibility(View.GONE);
        mUltimateRecyclerView.setVisibility(View.VISIBLE);
        mLblNoResult.setVisibility(View.GONE);
    }

    // Method to display no result view if data is not available
    private void noResultView() {
        mLytRetry.setVisibility(View.GONE);
        mUltimateRecyclerView.setVisibility(View.GONE);
        mLblNoResult.setVisibility(View.VISIBLE);

    }

    // Method to disable load more
    private void disableLoadmore() {
        mIsStillLoding = false;
        if (mUltimateRecyclerView.isLoadMoreEnabled()) {
            mUltimateRecyclerView.disableLoadmore();
        }
        mAdapter.notifyDataSetChanged();
    }

    // Method to display view at the beginning
    private void beginningView(){

        if(mLytRetry.getVisibility() == View.VISIBLE) {
            mLytRetry.setVisibility(View.GONE);
        }

        mPrgLoading.setVisibility(View.VISIBLE);

        // Clear mAdapter view
        mAdapter.clear(newsData);
        mAdapter.notifyDataSetChanged();

        // First make recyle view gone, if no result mTxtSearchFilter can click again
        mUltimateRecyclerView.setVisibility(View.GONE);

        // Start index begin from 0 again

        // Set mLblNoResult GONE
        mLblNoResult.setVisibility(View.GONE);

        // When mTxtSearchFilter click enter/search its means first time again
        mIsAppFirstLaunched = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRetry:
                haveResultView();
                mPrgLoading.setVisibility(View.VISIBLE);
                getNewsData(mKeyword);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.saveIntPreferences(getActivity(),
                Utils.ARG_DRAWER_PREFERENCE, Utils.ARG_PREFERENCES_DRAWER, 1);
    }

}

