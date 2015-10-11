package com.ocypode.application.activity;

import android.os.Bundle;
import android.os.Handler;

import com.ocypode.R;
import com.ocypode.application.activity.robo.AbstractRoboActivity;

import roboguice.inject.ContentView;

/**
 * Created by macksuel on 4/12/15.
 */
@ContentView(R.layout.activity_splashscreen)
public class SplashscreenActivity extends AbstractRoboActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pushActivity(MainActivity.class);
                SplashscreenActivity.this.finish();
            }
        }, 2000);
    }
}
