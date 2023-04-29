package com.roy.turbo.launcher.ui

import android.content.Intent
import com.roy.turbo.launcher.Utilities
import com.roy.turbo.launcher.WallpaperPickerActivity

class LauncherWallpaperPickerActivity : WallpaperPickerActivity() {
    override fun startActivityForResultSafely(
        intent: Intent,
        requestCode: Int
    ) {
        Utilities.startActivityForResultSafely(
            /* activity = */ this,
            /* intent = */ intent,
            /* requestCode = */ requestCode
        )
    }

    override fun enableRotation(): Boolean {
        return Utilities.isRotationEnabled(this)
    }
}
