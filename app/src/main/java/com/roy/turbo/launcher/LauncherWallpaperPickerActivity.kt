package com.roy.turbo.launcher

import android.content.Intent

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
