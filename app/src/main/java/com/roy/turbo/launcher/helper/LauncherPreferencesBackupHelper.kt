package com.roy.turbo.launcher.helper

import android.app.backup.BackupDataInputStream
import android.app.backup.SharedPreferencesBackupHelper
import android.content.Context

class LauncherPreferencesBackupHelper(
    context: Context?,
    sharedPreferencesKey: String?,
    private val mRestoreEnabled: Boolean
) : SharedPreferencesBackupHelper(context, sharedPreferencesKey) {

    override fun restoreEntity(data: BackupDataInputStream) {
        if (mRestoreEnabled) {
            super.restoreEntity(data)
        }
    }
}
