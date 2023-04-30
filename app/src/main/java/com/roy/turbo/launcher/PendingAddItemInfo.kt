package com.roy.turbo.launcher

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Parcelable

/**
 * We pass this object with a drag from the customization tray
 */
internal open class PendingAddItemInfo : ItemInfo() {
    /**
     * The component that will be created.
     */
    @JvmField
    var componentName: ComponentName? = null
}

internal class PendingAddShortcutInfo(
    var shortcutActivityInfo: ActivityInfo
) : PendingAddItemInfo() {
    override fun toString(): String {
        return "Shortcut: " + shortcutActivityInfo.packageName
    }
}

internal class PendingAddWidgetInfo : PendingAddItemInfo {
    @JvmField
    var minWidth: Int

    @JvmField
    var minHeight: Int

    @JvmField
    var minResizeWidth: Int

    @JvmField
    var minResizeHeight: Int

    @JvmField
    var previewImage: Int

    @JvmField
    var icon: Int

    @JvmField
    var info: AppWidgetProviderInfo

    @JvmField
    var boundWidget: AppWidgetHostView? = null

    @JvmField
    var bindOptions: Bundle? = null

    // Any configuration data that we want to pass to a configuration activity when
    // starting up a widget
    private var mimeType: String? = null
    private var configurationData: Parcelable? = null

    constructor(i: AppWidgetProviderInfo, dataMimeType: String?, data: Parcelable?) {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
        info = i
        componentName = i.provider
        minWidth = i.minWidth
        minHeight = i.minHeight
        minResizeWidth = i.minResizeWidth
        minResizeHeight = i.minResizeHeight
        previewImage = i.previewImage
        icon = i.icon
        if (dataMimeType != null && data != null) {
            mimeType = dataMimeType
            configurationData = data
        }
    }

    // Copy constructor
    constructor(copy: PendingAddWidgetInfo) {
        minWidth = copy.minWidth
        minHeight = copy.minHeight
        minResizeWidth = copy.minResizeWidth
        minResizeHeight = copy.minResizeHeight
        previewImage = copy.previewImage
        icon = copy.icon
        info = copy.info
        boundWidget = copy.boundWidget
        mimeType = copy.mimeType
        configurationData = copy.configurationData
        componentName = copy.componentName
        itemType = copy.itemType
        spanX = copy.spanX
        spanY = copy.spanY
        minSpanX = copy.minSpanX
        minSpanY = copy.minSpanY
        bindOptions = if (copy.bindOptions == null) null else copy.bindOptions?.clone() as Bundle?
    }

    override fun toString(): String {
        return "Widget: " + componentName?.toShortString()
    }
}
