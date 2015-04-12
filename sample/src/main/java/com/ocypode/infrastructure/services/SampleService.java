package com.ocypode.infrastructure.services;


import com.ocypode.domain.model.SampleModel;
import com.ocypode.volleyrestclient.infrastructure.handler.Handler;

/**
 * Created by macksuel on 4/6/15.
 */
public interface SampleService {

    void get(Handler<SampleModel> handler);
}
