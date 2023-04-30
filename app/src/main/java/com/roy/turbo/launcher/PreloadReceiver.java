package com.roy.turbo.launcher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class PreloadReceiver extends BroadcastReceiver {
    private static final String TAG = "PreloadReceiver";
    private static final boolean LOGD = false;

    public static final String EXTRA_WORKSPACE_NAME = "com.roy.turbo.launcher.action.EXTRA_WORKSPACE_NAME";

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onReceive(Context context, Intent intent) {
        final LauncherProvider provider = LauncherAppState.getLauncherProvider();
        if (provider != null) {
            String name = intent.getStringExtra(EXTRA_WORKSPACE_NAME);
            final int workspaceResId = !TextUtils.isEmpty(name) ? context.getResources().getIdentifier(name, "xml", "com.roy.turbo.launcher") : 0;
            if (LOGD) {
                Log.d(TAG, "workspace name: " + name + " id: " + workspaceResId);
            }
            new AsyncTask<Void, Void, Void>() {
                public Void doInBackground(Void ... args) {
                    provider.loadDefaultFavoritesIfNecessary(workspaceResId);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }
}
