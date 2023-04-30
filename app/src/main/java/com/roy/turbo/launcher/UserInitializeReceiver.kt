package com.roy.turbo.launcher

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Takes care of setting initial wallpaper for a user, by selecting the
 * first wallpaper that is not in use by another user.
 */
class UserInitializeReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        // TODO: initial wallpaper now that wallpapers are owned by another app
    }
}
