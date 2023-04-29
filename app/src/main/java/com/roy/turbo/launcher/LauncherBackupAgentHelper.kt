package com.roy.turbo.launcher

import android.app.backup.BackupAgentHelper
import android.app.backup.BackupManager
import android.content.Context
import android.provider.Settings

open class LauncherBackupAgentHelper : BackupAgentHelper() {
    override fun onDestroy() {
        // There is only one process accessing this preference file, but the restore
        // modifies the file outside the normal codepaths, so it looks like another
        // process.  This forces a reload of the file, in case this process persists.
        val spKey = LauncherAppState.getSharedPreferencesKey()
        val sp = getSharedPreferences(spKey, MODE_MULTI_PROCESS)
        super.onDestroy()
    }

    override fun onCreate() {
        val restoreEnabled =
            0 != Settings.Secure.getInt(contentResolver, SETTING_RESTORE_ENABLED, 0)
        //        if (VERBOSE) {
//            Log.v(TAG, "restore is " + (restoreEnabled ? "enabled" : "disabled"));
//        }
        addHelper(
            LauncherBackupHelper.LAUNCHER_PREFS_PREFIX,
            LauncherPreferencesBackupHelper(
                this,
                LauncherAppState.getSharedPreferencesKey(),
                restoreEnabled
            )
        )
        addHelper(
            LauncherBackupHelper.LAUNCHER_PREFIX,
            LauncherBackupHelper(this, restoreEnabled)
        )
    }

    companion object {
        //    private static final String TAG = "LauncherBackupAgentHelper";
        const val VERBOSE = true
        const val DEBUG = false
        private var sBackupManager: BackupManager? = null
        protected const val SETTING_RESTORE_ENABLED = "launcher_restore_enabled"

        /**
         * Notify the backup manager that out database is dirty.
         *
         * <P>This does not force an immediate backup.
         *
         * @param context application context
        </P> */
        @JvmStatic
        fun dataChanged(context: Context?) {
            if (sBackupManager == null) {
                sBackupManager = BackupManager(context)
            }
            sBackupManager?.dataChanged()
        }
    }
}
