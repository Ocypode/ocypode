package com.ocypode.volleyrestclient.infrastructure.request.gson;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ocypode.volleyrestclient.infrastructure.handler.Handler;
import com.ocypode.volleyrestclient.infrastructure.request.RequestHelper;
import com.ocypode.volleyrestclient.infrastructure.request.RequestHelper.ListenerCatchingException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Listener<T> listener;
    private final JSONObject jsonObject;

    public GsonRequest(int method, String url, Class<T> clazz,
                       final Handler<?> handler, ListenerCatchingException<T> listener) {
        this(method, url, clazz, null, RequestHelper.getHeaders(), RequestHelper.getParams(),
                RequestHelper.createResponseListener(handler, listener),
                RequestHelper.createErrorResponseListener(handler));
    }

    public GsonRequest(int method, String url, JSONObject jsonObject, Class<T> clazz,
                       final Handler<?> handler, ListenerCatchingException<T> listener) {
        this(method, url, clazz, jsonObject, RequestHelper.getHeaders(), RequestHelper.getParams(),
                RequestHelper.createResponseListener(handler, listener),
                RequestHelper.createErrorResponseListener(handler));
    }

    public GsonRequest(int method, String url, Class<T> clazz,
                       final Handler<?> handler, Map<String, String> headers, ListenerCatchingException<T> listener) {
        this(method, url, clazz, null, headers, null,
                RequestHelper.createResponseListener(handler, listener),
                RequestHelper.createErrorResponseListener(handler));
    }

    public GsonRequest(int method, String url, Class<T> clazz,
                       final Handler<?> handler, Map<String, String> headers, Map<String, String> params, ListenerCatchingException<T> listener) {
        this(method, url, clazz, null, headers, params,
                RequestHelper.createResponseListener(handler, listener),
                RequestHelper.createErrorResponseListener(handler));
    }


    private GsonRequest(int method, String url, Class<T> clazz, JSONObject jsonObject,
                        Map<String, String> headers, Map<String, String> params, Listener<T> listener,
                        ErrorListener errorListener) {
        super(method, url, errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class,
                new TimestampDeserializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.gson = gsonBuilder.create();
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;

        this.jsonObject = jsonObject;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }


    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (jsonObject != null) {
            try {
                return jsonObject.toString().getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException e) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        jsonObject, PROTOCOL_CHARSET);
                return null;
            }
        }
        return super.getBody();
    }

    @Override
    public String getBodyContentType() {
        if (jsonObject != null) {
            return PROTOCOL_CONTENT_TYPE;
        }
        return super.getBodyContentType();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            if (json.equals("{}")) {
                return Response.error(new ParseError());
            }
            return Response.success(gson.fromJson(json, clazz),
                    RequestHelper.parseIgnoreCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
