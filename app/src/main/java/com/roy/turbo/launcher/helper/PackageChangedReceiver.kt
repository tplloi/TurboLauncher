package com.roy.turbo.launcher.helper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.roy.turbo.launcher.LauncherAppState
import com.roy.turbo.launcher.WidgetPreviewLoader

class PackageChangedReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        if (packageName.isNullOrEmpty()) {
            return
        }
        LauncherAppState.setApplicationContext(context.applicationContext)
        val app = LauncherAppState.getInstance()
        WidgetPreviewLoader.removePackageFromDb(app.widgetPreviewCacheDb, packageName)
    }
}
