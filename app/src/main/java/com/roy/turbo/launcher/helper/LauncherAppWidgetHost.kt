package com.roy.turbo.launcher.helper

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import com.roy.turbo.launcher.Launcher
import com.roy.turbo.launcher.view.LauncherAppWidgetHostView
import com.roy.turbo.launcher.LauncherModel

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
