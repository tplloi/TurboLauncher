package com.roy.turbo.launcher.helper

import android.view.View
import com.roy.turbo.launcher.LauncherAppState

class CheckLongPressHelper(private val mView: View) {
    private var mHasPerformedLongPress = false
    private var mPendingCheckForLongPress: CheckForLongPress? = null

    internal inner class CheckForLongPress : Runnable {
        override fun run() {
            if (mView.parent != null && mView.hasWindowFocus()
                && !mHasPerformedLongPress
            ) {
                if (mView.performLongClick()) {
                    mView.isPressed = false
                    mHasPerformedLongPress = true
                }
            }
        }
    }

    fun postCheckForLongPress() {
        mHasPerformedLongPress = false
        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = CheckForLongPress()
        }
        mView.postDelayed(
            mPendingCheckForLongPress,
            LauncherAppState.getInstance().longPressTimeout.toLong()
        )
    }

    fun cancelLongPress() {
        mHasPerformedLongPress = false
        if (mPendingCheckForLongPress != null) {
            mView.removeCallbacks(mPendingCheckForLongPress)
            mPendingCheckForLongPress = null
        }
    }

    fun hasPerformedLongPress(): Boolean {
        return mHasPerformedLongPress
    }
}
