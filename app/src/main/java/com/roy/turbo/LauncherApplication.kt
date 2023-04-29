package com.roy.turbo

import android.app.Application
import com.roy.turbo.launcher.LauncherAppState

//TODO ic_launcher
//TODO ad
//TODO firebase
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
