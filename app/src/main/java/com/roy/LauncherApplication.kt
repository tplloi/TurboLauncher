package com.roy

import android.app.Application
import com.roy.turbo.launcher.LauncherAppState

//TODO ad
//TODO firebase
//TODO rate, more app, share app
//TODO policy
//TODO keystore
//TODO rename app

//done
//ic_launcher
//proguard
class LauncherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LauncherAppState.setApplicationContext(this)
        LauncherAppState.getInstance()
    }

    override fun onTerminate() {
        super.onTerminate()
        LauncherAppState.getInstance().onTerminate()
    }
}
