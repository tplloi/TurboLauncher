package com.roy.turbo.launcher

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap

internal class AppInfo : ItemInfo {
    @JvmField
    var intent: Intent? = null

    @JvmField
    var iconBitmap: Bitmap? = null

    @JvmField
    var firstInstallTime: Long = 0

    @JvmField
    var componentName: ComponentName? = null

    @JvmField
    var flags = 0

    constructor() {
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT
    }

    public override fun getIntent(): Intent? {
        return intent
    }

    override fun getRestoredIntent(): Intent? {
        return null
    }

    /**
     * Must not hold the Context.
     */
    constructor(
        pm: PackageManager, info: ResolveInfo, iconCache: IconCache,
        labelCache: HashMap<Any?, CharSequence?>?,
    ) {
        val packageName = info.activityInfo.applicationInfo.packageName
        componentName = ComponentName(
            packageName,
            info.activityInfo.name
        )
        container = NO_ID.toLong()
        setActivity(
            componentName, Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
        try {
            val pi = pm.getPackageInfo(packageName, 0)
            flags = initFlags(pi)
            firstInstallTime = initFirstInstallTime(pi)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        iconCache.getTitleAndIcon(this, info, labelCache)
    }

    constructor(info: AppInfo) : super(info) {
        componentName = info.componentName
        title = info.title.toString()
        intent = Intent(info.intent)
        flags = info.flags
        firstInstallTime = info.firstInstallTime
    }

    /**
     * Creates the application intent based on a component name and various
     * launch flags. Sets [.itemType] to
     * [LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION].
     *
     * @param className   the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    fun setActivity(className: ComponentName?, launchFlags: Int) {
        intent = Intent(Intent.ACTION_MAIN)
        intent?.let {
            it.addCategory(Intent.CATEGORY_LAUNCHER)
            it.component = className
            it.flags = launchFlags
        }
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION
    }

    override fun toString(): String {
        return ("ApplicationInfo(title=" + title.toString() + " id=" + id
                + " type=" + itemType + " container=" + container
                + " screen=" + screenId + " cellX=" + cellX + " cellY=" + cellY
                + " spanX=" + spanX + " spanY=" + spanY + " dropPos=" + dropPos
                + ")")
    }

    fun makeShortcut(): ShortcutInfo {
        return ShortcutInfo(this)
    }

    companion object {
        const val DOWNLOADED_FLAG = 1
        private const val UPDATED_SYSTEM_APP_FLAG = 2

        @JvmStatic
        fun initFlags(pi: PackageInfo): Int {
            val appFlags = pi.applicationInfo?.flags ?: 0
            var flags = 0
            if (appFlags and ApplicationInfo.FLAG_SYSTEM == 0) {
                flags = flags or DOWNLOADED_FLAG
                if (appFlags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                    flags = flags or UPDATED_SYSTEM_APP_FLAG
                }
            }
            return flags
        }

        @JvmStatic
        fun initFirstInstallTime(pi: PackageInfo): Long {
            return pi.firstInstallTime
        }
    }
}
