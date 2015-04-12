package com.ocypode.application;

import android.app.Application;

import roboguice.RoboGuice;

/**
 * Created by macksuel on 4/12/15.
 */
public class MainApplication extends Application {

    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
