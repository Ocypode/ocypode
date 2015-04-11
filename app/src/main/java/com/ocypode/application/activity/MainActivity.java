package com.ocypode.application.activity;

import android.os.Bundle;

import com.ocypode.R;
import com.ocypode.application.activity.robo.AbstractRoboActivity;

import roboguice.inject.ContentView;

@ContentView(R.layout.activity_main)
public class MainActivity extends AbstractRoboActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
