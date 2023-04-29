package com.roy.turbo.launcher

import android.support.v4.widget.AutoScrollHelper
import android.widget.ScrollView

/**
 * An implementation of [AutoScrollHelper] that knows how to scroll
 * through a [Folder].
 */
class FolderAutoScrollHelper(private val mTarget: ScrollView) : AutoScrollHelper(
    mTarget
) {
    init {
        setActivationDelay(0)
        setEdgeType(EDGE_TYPE_INSIDE_EXTEND)
        isExclusive = true
        setMaximumVelocity(MAX_SCROLL_VELOCITY, MAX_SCROLL_VELOCITY)
        setRampDownDuration(0)
        setRampUpDuration(0)
    }

    override fun scrollTargetBy(deltaX: Int, deltaY: Int) {
        mTarget.scrollBy(deltaX, deltaY)
    }

    override fun canTargetScrollHorizontally(direction: Int): Boolean {
        // List do not scroll horizontally.
        return false
    }

    override fun canTargetScrollVertically(direction: Int): Boolean {
        return mTarget.canScrollVertically(direction)
    }

    companion object {
        private const val MAX_SCROLL_VELOCITY = 1500f
    }
}
