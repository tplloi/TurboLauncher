package com.roy

import android.app.Application
import com.roy.turbo.launcher.LauncherAppState

// https://github.com/tplloi/TurboLauncher
//TODO biometric
//TODO firebase
//TODO save position of drawer

//done
//ic_launcher
//proguard
//keystore
//rename app
//auto select home
//rate, more app, share app
//leak canary //failed
//policy
//ad
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
