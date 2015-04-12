package com.ocypode.infrastructure.clients;

import android.content.Context;

import com.android.volley.Request;
import com.ocypode.domain.model.SampleModel;
import com.ocypode.infrastructure.services.SampleService;
import com.ocypode.volleyrestclient.infrastructure.handler.Handler;
import com.ocypode.volleyrestclient.infrastructure.request.RequestHelper;
import com.ocypode.volleyrestclient.infrastructure.request.gson.GsonRequest;

import org.json.JSONException;

/**
 * Created by macksuel on 4/12/15.
 */
public class SampleRestClient extends AbstractVolleyRestClient implements SampleService {

    public SampleRestClient(Context context) {
        super(context);
    }

    @Override
    public void get(final Handler<SampleModel> handler) {
        String url = createUrl("jedi.json");

        GsonRequest<SampleModel> request = new GsonRequest<>(
                Request.Method.GET, url, SampleModel.class, handler, new RequestHelper.ListenerCatchingException<SampleModel>() {

            @Override
            public void onResponse(SampleModel response) throws JSONException {
                handler.onSuccess(response);
            }
        });

        addToRequestQueue(request);
    }
}
