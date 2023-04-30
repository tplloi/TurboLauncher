package com.roy

import android.app.Application
import com.roy.turbo.launcher.LauncherAppState

//TODO ic_launcher
//TODO ad
//TODO firebase
//TODO rate, more app, share app
//TODO policy
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
