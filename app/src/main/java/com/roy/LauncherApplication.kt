package com.roy

import android.app.Application
import com.roy.turbo.launcher.LauncherAppState

//TODO ad
//TODO biometric
//TODO firebase
//TODO save position of drawer

//TODO policy

//done
//ic_launcher
//proguard
//keystore
//rename app
//auto select home
//rate, more app, share app
//leak canary //failed
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
