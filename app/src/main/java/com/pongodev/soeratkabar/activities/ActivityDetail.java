package com.pongodev.soeratkabar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pongodev.soeratkabar.R;
import com.pongodev.soeratkabar.libs.MySingleton;
import com.pongodev.soeratkabar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Design and developed by pongodev.com
 *
 * FragmentCategory is created to display places by selected
 * category in recyclerview. Created using AppCompatActivity.
 */
public class ActivityDetail extends AppCompatActivity implements OnClickListener {

    // Create tag for log
    private final String TAG = ActivityDetail.class.getSimpleName();

    // Declare view objects
    private TextView lblTitle, lblDate, lblAuthor, txtDesc;
    private ImageView imgThumbnail;
    private RelativeLayout lytDetail;
    private LinearLayout lytRetry;
    private ImageLoader mImageLoader;

    private String mTitle;
    private String mExcerpt;
    private String mDate;
    private String mAuthor;
    private String mImgThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i    = getIntent();
        String mGetNewsId = i.getStringExtra(Utils.EXTRA_NEWS_ID);
        mImageLoader= MySingleton.getInstance(this).getImageLoader();

        // Connect view objects and xml ids
        lblTitle		= (TextView) findViewById(R.id.lblTitle);
        lblDate			= (TextView) findViewById(R.id.lblDate);
        lblAuthor		= (TextView) findViewById(R.id.lblAuthor);
        lytDetail		= (RelativeLayout) findViewById(R.id.lytDetail);
        imgThumbnail 	= (ImageView) findViewById(R.id.imgThumbnail);
        lytRetry 		= (LinearLayout) findViewById(R.id.lytRetry);
        Button btnRetry = (Button) findViewById(R.id.btnRetry);
        txtDesc 		= (TextView) findViewById(R.id.txtDesc);
        Button btnWeb   = (Button) findViewById(R.id.btnWeb);

        btnRetry.setOnClickListener(this);
        btnWeb.setOnClickListener(this);
        getLocationDetail(mGetNewsId);
    }

    // Method to get location detail from server
    private void getLocationDetail(String mGetNewsId){

        // http://pongodev.com/category/support/?json=get_post&post_id=3774
        String url = Utils.API_WORDPRESS+Utils.VALUE_GET_POST+"&"+
                Utils.PARAM_POST_ID+mGetNewsId;
Log.e("testing","url= "+url);
        JsonObjectRequest request = new JsonObjectRequest(url, null,
            new Response.Listener<JSONObject>() {
                String mStatus;
                JSONObject newsPostObject;
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(Utils.TAG_PONGODEV + TAG, "onResponse " + response.toString());
                    try {
                        mStatus = response.getString(Utils.KEY_STATUS);
                        if (mStatus.equals("ok")) {
                            lytRetry.setVisibility(View.GONE);
                            newsPostObject = response.getJSONObject(Utils.OBJECT_NEWS_POST);

                            // Get Title
                            mTitle = newsPostObject.getString(Utils.KEY_TITLE);
                            Log.e("testing","mTitle= "+mTitle);

                            // Get Excerpt
                            mExcerpt = newsPostObject.getString(Utils.KEY_EXCERPT);

                            // Get Date
                            mDate = newsPostObject.getString(Utils.KEY_DATE);

                            // Get author
                            JSONObject dataAuthorObject =
                                    newsPostObject.getJSONObject(Utils.KEY_NEWS_AUTHOR);
                            mAuthor	= dataAuthorObject.getString(Utils.KEY_NEWS_AUTHOR_NAME);
                            Log.e("testing","mAuthor= "+mAuthor);

                            // Get Image
                            JSONArray dataAttchArray =
                                    newsPostObject.getJSONArray(Utils.ARRAY_ATTACHMENTS);

                            // Condition if length of attach array 0
                            if(dataAttchArray.length()==0){
                                mImgThumbnail= Utils.VALUE_DEFAULT;
                            } else {
                                JSONObject attachObject = dataAttchArray.getJSONObject(0);
                                JSONObject dataImageObject =
                                        attachObject.getJSONObject(Utils.KEY_NEWS_IMAGE);
                                JSONObject dataThumbnailsObject =
                                        dataImageObject.getJSONObject(Utils.KEY_NEWS_FULL_THUMB);
                                mImgThumbnail= dataThumbnailsObject.getString(Utils.KEY_NEWS_URL);
                            }

                            Log.e("testing","mImgThumbnail= "+mImgThumbnail);

                            // Display Data
                            lytDetail.setVisibility(View.VISIBLE);
                            lytRetry.setVisibility(View.GONE);
                            lblTitle.setText(Html.fromHtml(mTitle));
                            lblAuthor.setText("by " + mAuthor);
                            lblDate.setText(mDate);

                            txtDesc.setText(Html.fromHtml(mExcerpt));

                            mImageLoader.get(mImgThumbnail,
                                    com.android.volley.toolbox.ImageLoader.getImageListener
                                            (imgThumbnail, R.mipmap.ic_launcher, R.mipmap.ic_launcher));



                        }
                    } catch (JSONException e) {
                        Log.d(Utils.TAG_PONGODEV + TAG,
                                "onResponse JSON Parsing error: " + e.getMessage());
                    }
                }
            },

            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(Utils.TAG_PONGODEV + TAG,
                            "onResponse Response.ErrorListener()= " + error.toString());
                    // If response error, display retry layout
                    // and hide other views
                    lytRetry.setVisibility(View.VISIBLE);
                }
            }
        );

        // Set request timeout
        request.setRetryPolicy(new DefaultRetryPolicy(Utils.ARG_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).getRequestQueue().add(request);
    }

    @Override
    public void onClick(View v) {

    }
}
