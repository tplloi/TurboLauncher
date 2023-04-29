package com.roy.turbo.launcher;

import android.app.Application;

//TODO ic_launcher
//TODO ad
//TODO firebase
public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LauncherAppState.setApplicationContext(this);
        LauncherAppState.getInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LauncherAppState.getInstance().onTerminate();
    }
}