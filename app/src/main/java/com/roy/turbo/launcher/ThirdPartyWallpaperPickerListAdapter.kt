package com.roy.turbo.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.TextView
import com.roy.turbo.launcher.WallpaperPickerActivity.WallpaperTileInfo

class ThirdPartyWallpaperPickerListAdapter(
    context: Context
) : BaseAdapter(), ListAdapter {
    private val mInflater: LayoutInflater
    private val mPackageManager: PackageManager
    private val mIconSize: Int
    private val mThirdPartyWallpaperPickers: MutableList<ThirdPartyWallpaperTile> = ArrayList()

    class ThirdPartyWallpaperTile(val mResolveInfo: ResolveInfo) : WallpaperTileInfo() {
        override fun onClick(a: WallpaperPickerActivity) {
            val itemComponentName =
                ComponentName(mResolveInfo.activityInfo.packageName, mResolveInfo.activityInfo.name)
            val launchIntent = Intent(Intent.ACTION_SET_WALLPAPER)
            launchIntent.component = itemComponentName
            a.startActivityForResultSafely(
                launchIntent,
                WallpaperPickerActivity.PICK_WALLPAPER_THIRD_PARTY_ACTIVITY
            )
        }
    }

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mPackageManager = context.packageManager
        mIconSize = context.resources.getDimensionPixelSize(R.dimen.wallpaperItemIconSize)
        val pm = mPackageManager
        val pickWallpaperIntent = Intent(Intent.ACTION_SET_WALLPAPER)
        val apps = pm.queryIntentActivities(pickWallpaperIntent, 0)

        // Get list of image picker intents
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        val imagePickerActivities = pm.queryIntentActivities(pickImageIntent, 0)
        val imageActivities = arrayOfNulls<ComponentName>(imagePickerActivities.size)
        for (i in imagePickerActivities.indices) {
            val activityInfo = imagePickerActivities[i].activityInfo
            imageActivities[i] = ComponentName(activityInfo.packageName, activityInfo.name)
        }
        outerLoop@ for (info in apps) {
            val itemComponentName =
                ComponentName(info.activityInfo.packageName, info.activityInfo.name)
            val itemPackageName = itemComponentName.packageName
            // Exclude anything from our own package, and the old Launcher,
            // and live wallpaper picker
            if (itemPackageName == context.packageName || itemPackageName == "com.android.launcher" || itemPackageName == "com.android.wallpaper.livepicker") {
                continue
            }
            // Exclude any package that already responds to the image picker intent
            for (imagePickerActivityInfo in imagePickerActivities) {
                if (itemPackageName ==
                    imagePickerActivityInfo.activityInfo.packageName
                ) {
                    continue@outerLoop
                }
            }
            mThirdPartyWallpaperPickers.add(ThirdPartyWallpaperTile(info))
        }
    }

    override fun getCount(): Int {
        return mThirdPartyWallpaperPickers.size
    }

    override fun getItem(position: Int): ThirdPartyWallpaperTile {
        return mThirdPartyWallpaperPickers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val view: View = convertView
        WallpaperPickerActivity.setWallpaperItemPaddingToZero(view as FrameLayout)
        val info = mThirdPartyWallpaperPickers[position].mResolveInfo
        val label = view.findViewById<View>(R.id.wallpaper_item_label) as TextView
        label.text = info.loadLabel(mPackageManager)
        val icon = info.loadIcon(mPackageManager)
        icon.bounds = Rect(0, 0, mIconSize, mIconSize)
        label.setCompoundDrawables(null, icon, null, null)
        return view
    }
}