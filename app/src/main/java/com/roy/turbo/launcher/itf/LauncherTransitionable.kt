package com.roy.turbo.launcher.itf

import android.view.View
import com.roy.turbo.launcher.Launcher

interface LauncherTransitionable {
    val content: View?

    fun onLauncherTransitionPrepare(
        l: Launcher?, animated: Boolean,
        toWorkspace: Boolean
    )

    fun onLauncherTransitionStart(
        l: Launcher?, animated:
        Boolean,
        toWorkspace: Boolean
    )

    fun onLauncherTransitionStep(l: Launcher?, t: Float)

    fun onLauncherTransitionEnd(
        l: Launcher?,
        animated: Boolean,
        toWorkspace: Boolean
    )
}
