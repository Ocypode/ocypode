package com.ocypode.application.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.inject.Inject;
import com.ocypode.R;
import com.ocypode.application.activity.robo.AbstractRoboActivity;
import com.ocypode.domain.model.SampleModel;
import com.ocypode.infrastructure.services.SampleService;
import com.ocypode.volleyrestclient.component.image.ImageViewVolley;
import com.ocypode.volleyrestclient.infrastructure.handler.Handler;
import com.ocypode.volleyrestclient.infrastructure.request.ImageLoaderVolley;
import com.ocypode.volleyrestclient.infrastructure.request.RequestQueueVolley;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends AbstractRoboActivity {

    @Inject
    private SampleService mSampleService;

    @InjectView(R.id.testimage)
    private ImageViewVolley mImageViewVolley;

    private ImageLoaderVolley mImageLoaderVolley;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoaderVolley = ImageLoaderVolley.getInstance(this, RequestQueueVolley.getInstance(this));
        mImageViewVolley.setImageUrl("https://lh5.ggpht.com/yLZ71UFYYvCfDhJZr3CnA_MKXS7mAnaJXPkFGj8ZzYWahvJYY-90rU9Xey_DEQ6AFN7z=w300", mImageLoaderVolley.newImageLoaderInDisk());

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
