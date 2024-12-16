package com.roy

import android.app.Application
import com.roy.turbo.launcher.LauncherAppState

//TODO roy93~ biometric
//TODO roy93~ firebase
//TODO roy93~ save position of drawer
//TODO roy93~ ic_launcher
//TODO roy93~ proguard
//TODO roy93~ keystore
//TODO roy93~ rename app
//TODO roy93~ auto select home
//TODO roy93~ rate, more app, share app
//TODO roy93~ leak canary //failed
//TODO roy93~ policy
//TODO roy93~ ad
//TODO roy93~ done mckimquyen

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
