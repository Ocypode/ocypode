package com.ocypode.volleyrestclient.infrastructure.request;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.ocypode.volleyrestclient.infrastructure.handler.Handler;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {

	public interface ListenerCatchingException<T> {
        public void onResponse(T response) throws JSONException;
    }
	
	public static ErrorListener createErrorResponseListener(final Handler<?> handler) {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				handler.onFail(error);
			}
		};
	}

	public static <T> Listener<T> createResponseListener(final Handler<?> handler, 
			final ListenerCatchingException<T> listener) {
		return new Listener<T>() {

			@Override
			public void onResponse(T response) {
				try {
					VolleyLog.v("Response:%n %s", response);
					listener.onResponse(response);
				} catch (JSONException e) {
					handler.onFail(e);
				}
			}
		};
	}

	/**
	 * This method has to be used in extreme situation
	 * @return
	 */
	public static DefaultRetryPolicy create30SecondsRetryPolicyTimeout() {
		return new DefaultRetryPolicy(30000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
	}
	
	/**
	 * Extracts a {@link com.android.volley.Cache.Entry} from a {@link com.android.volley.NetworkResponse}.
	 * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
	 * @param response The network response to parse headers from
	 * @return a cache entry for the given response, or null if the response is not cacheable.
	 */
	public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
	    long now = System.currentTimeMillis();

	    Map<String, String> headers = response.headers;
	    long serverDate = 0;
	    String serverEtag = null;
	    String headerValue;

	    headerValue = headers.get("Date");
	    if (headerValue != null) {
	        serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
	    }

	    serverEtag = headers.get("ETag");

	    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
	    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
	    final long softExpire = now + cacheHitButRefreshed;
	    final long ttl = now + cacheExpired;

	    Cache.Entry entry = new Cache.Entry();
	    entry.data = response.data;
	    entry.etag = serverEtag;
	    entry.softTtl = softExpire;
	    entry.ttl = ttl;
	    entry.serverDate = serverDate;
	    entry.responseHeaders = headers;

	    return entry;
	}

	public static Map<String, String> getHeaders() {
        HashMap headers = new HashMap<String, String>();

        return headers;
	}

    public static Map<String, String> getParams() {
        Map<String, String> pars = new HashMap<>();

        return pars;
    }
}
