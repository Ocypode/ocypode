package com.ocypode.infrastructure.clients;

import android.content.Context;

import com.google.gson.Gson;
import com.ocypode.volleyrestclient.infrastructure.request.RequestQueueVolley;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class AbstractVolleyRestClient {

    private static final String END_POINT_URI = "https://dl.dropboxusercontent.com/u/5135185/dumbledroid/";

    private RequestQueueVolley mRequestQueueVolley;

    public AbstractVolleyRestClient(Context context) {
        mRequestQueueVolley = RequestQueueVolley.getInstance(context);
    }

	protected <T> void addToRequestQueue(com.android.volley.Request<T> request) {
		request.setShouldCache(false);
        mRequestQueueVolley.addToRequestQueue(request);
	}
	
	protected String createUrl(String resource) {
		return END_POINT_URI + resource;
	}
	
	protected JSONObject parseToJSONObject(Object obj) {
		String json = new Gson().toJson(obj);
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
