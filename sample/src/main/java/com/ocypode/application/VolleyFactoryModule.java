package com.ocypode.application;

import android.app.Application;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.ocypode.infrastructure.clients.SampleRestClient;
import com.ocypode.infrastructure.services.SampleService;

public class VolleyFactoryModule extends AbstractModule {

    private Context mContext;

    public VolleyFactoryModule(Application application) {
        this.mContext = application.getApplicationContext();
    }

    @Override
    public void configure() {
        bind(SampleService.class).toInstance(new SampleRestClient(mContext));
    }
}
