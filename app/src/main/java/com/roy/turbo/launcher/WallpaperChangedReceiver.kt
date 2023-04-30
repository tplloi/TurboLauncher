package com.roy.turbo.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WallpaperChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, data: Intent) {

        LauncherAppState.setApplicationContext(context.applicationContext)
        val appState = LauncherAppState.getInstance()
        appState.onWallpaperChanged()
    }
}
