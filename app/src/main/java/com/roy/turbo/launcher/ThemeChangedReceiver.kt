package com.roy.turbo.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.roy.turbo.launcher.WidgetPreviewLoader.CacheDb

class ThemeChangedReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val app = LauncherAppState.getInstance()
        clearWidgetPreviewCache(context)
        app.recreateWidgetPreviewDb()
        app.iconCache.flush()
        app.model.forceReload()
    }

    private fun clearWidgetPreviewCache(context: Context) {
        val files = context.cacheDir.listFiles()
        if (files != null) {
            for (f in files) {
                if (!f.isDirectory && f.name.startsWith(CacheDb.DB_NAME)) f.delete()
            }
        }
    }
}
