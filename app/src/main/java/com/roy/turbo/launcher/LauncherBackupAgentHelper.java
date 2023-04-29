package com.roy.turbo.launcher;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

public class LauncherBackupAgentHelper extends BackupAgentHelper {

//    private static final String TAG = "LauncherBackupAgentHelper";
    static final boolean VERBOSE = true;
    static final boolean DEBUG = false;

    private static BackupManager sBackupManager;

    protected static final String SETTING_RESTORE_ENABLED = "launcher_restore_enabled";

    /**
     * Notify the backup manager that out database is dirty.
     *
     * <P>This does not force an immediate backup.
     *
     * @param context application context
     */
    public static void dataChanged(Context context) {
        if (sBackupManager == null) {
            sBackupManager = new BackupManager(context);
        }
        sBackupManager.dataChanged();
    }

    @Override
    public void onDestroy() {
        // There is only one process accessing this preference file, but the restore
        // modifies the file outside the normal codepaths, so it looks like another
        // process.  This forces a reload of the file, in case this process persists.
        String spKey = LauncherAppState.getSharedPreferencesKey();
        SharedPreferences sp = getSharedPreferences(spKey, Context.MODE_MULTI_PROCESS);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        boolean restoreEnabled = 0 != Settings.Secure.getInt(getContentResolver(), SETTING_RESTORE_ENABLED, 0);
//        if (VERBOSE) {
//            Log.v(TAG, "restore is " + (restoreEnabled ? "enabled" : "disabled"));
//        }

        addHelper(LauncherBackupHelper.LAUNCHER_PREFS_PREFIX,
                new LauncherPreferencesBackupHelper(this, LauncherAppState.getSharedPreferencesKey(), restoreEnabled));
        addHelper(LauncherBackupHelper.LAUNCHER_PREFIX,
                new LauncherBackupHelper(this, restoreEnabled));
    }
}
