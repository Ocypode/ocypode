package com.ocypode.volleyrestclient.infrastructure.request;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Caching request programmatically   
 * http://stackoverflow.com/questions/16781244/android-volley-jsonobjectrequest-caching
 * @author Jairo Junior - jairobjunior@gmail.com
 */
public class RequestQueueVolley {

    public static final String TAG = "VolleyPatterns";

    private static RequestQueueVolley mInstance;

    private RequestQueue mRequestQueue;

    private Context mContext;

    private RequestQueueVolley(Context context) {
        mContext = context;
    }

    public static RequestQueueVolley getInstance(Context context) {
        synchronized (context) {
            if (mInstance == null) {
                mInstance = new RequestQueueVolley(context);
            }
            return mInstance;
        }
    }

    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        // set the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", request.getUrl());

        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        // set the default tag if tag is empty
        request.setTag(TAG);

        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}