package com.andruid.magic.makeitspeak.application;

import android.app.Application;

import com.andruid.magic.makeitspeak.BuildConfig;

import timber.log.Timber;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }
}