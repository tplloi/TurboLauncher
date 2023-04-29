package com.roy.turbo.launcher

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

class LauncherAppWidgetHost(var mLauncher: Launcher, hostId: Int) :
    AppWidgetHost(mLauncher, hostId) {

    override fun onCreateView(
        context: Context,
        appWidgetId: Int,
        appWidget: AppWidgetProviderInfo
    ): AppWidgetHostView {
        return LauncherAppWidgetHostView(context)
    }

    override fun stopListening() {
        super.stopListening()
        clearViews()
    }

    override fun onProvidersChanged() {
        mLauncher.bindPackagesUpdated(LauncherModel.getSortedWidgetsAndShortcuts(mLauncher))
    }
}
