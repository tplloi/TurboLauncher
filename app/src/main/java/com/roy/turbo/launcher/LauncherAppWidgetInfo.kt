package com.roy.turbo.launcher

import android.appwidget.AppWidgetHostView
import android.content.ComponentName
import android.content.ContentValues

/**
 * Represents a widget (either instantiated or about to be) in the Launcher.
 */
internal class LauncherAppWidgetInfo(appWidgetId: Int, providerName: ComponentName) : ItemInfo() {
    /*
     * Indicates that the widget hasn't been instantiated yet.
     */
    //    static final int NO_ID = -1;
    /**
     * Identifier for this widget when talking with
     * [android.appwidget.AppWidgetManager] for updates.
     */
    @JvmField
    var appWidgetId: Int

    @JvmField
    var providerName: ComponentName

    // TODO: Are these necessary here?
    @JvmField
    var minWidth = -1

    @JvmField
    var minHeight = -1
    private var mHasNotifiedInitialWidgetSizeChanged = false

    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    @JvmField
    var hostView: AppWidgetHostView? = null

    init {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
        this.appWidgetId = appWidgetId
        this.providerName = providerName

        // Since the widget isn't instantiated yet, we don't know these values. Set them to -1
        // to indicate that they should be calculated based on the layout and minWidth/minHeight
        spanX = -1
        spanY = -1
    }

    public override fun onAddToDatabase(values: ContentValues) {
        super.onAddToDatabase(values)
        values.put(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId)
        values.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, providerName.flattenToString())
    }

    /**
     * When we bind the widget, we should notify the widget that the size has changed if we have not
     * done so already (only really for default workspace widgets).
     */
    fun onBindAppWidget(launcher: Launcher?) {
        if (!mHasNotifiedInitialWidgetSizeChanged) {
            notifyWidgetSizeChanged(launcher)
        }
    }

    /**
     * Trigger an update callback to the widget to notify it that its size has changed.
     */
    fun notifyWidgetSizeChanged(launcher: Launcher?) {
        AppWidgetResizeFrame.updateWidgetSizeRanges(hostView, launcher, spanX, spanY)
        mHasNotifiedInitialWidgetSizeChanged = true
    }

    override fun toString(): String {
        return "AppWidget(id=$appWidgetId)"
    }

    public override fun unbind() {
        super.unbind()
        hostView = null
    }
}
