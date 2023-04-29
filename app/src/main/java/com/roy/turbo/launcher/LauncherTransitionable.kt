package com.roy.turbo.launcher

import android.view.View

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
