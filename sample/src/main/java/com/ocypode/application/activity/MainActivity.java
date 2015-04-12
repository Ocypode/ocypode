package com.ocypode.application.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.inject.Inject;
import com.ocypode.R;
import com.ocypode.application.activity.robo.AbstractRoboActivity;
import com.ocypode.infrastructure.services.SampleService;
import com.ocypode.domain.model.SampleModel;
import com.ocypode.volleyrestclient.infrastructure.handler.Handler;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_main)
public class MainActivity extends AbstractRoboActivity {

    @Inject
    private SampleService mSampleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(MainActivity.this, "Loading JSON data...", Toast.LENGTH_SHORT).show();
        mSampleService.get(new Handler<SampleModel>() {
            @Override
            public void onSuccess(SampleModel response) {
                Toast.makeText(MainActivity.this, "JSON data loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Exception error) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
