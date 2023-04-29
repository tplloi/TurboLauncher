package com.roy.turbo.launcher

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

//done 2023.04.29
class PageIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    private val mLayoutInflater: LayoutInflater
    private val mWindowRange = IntArray(2)
    private val mMaxWindowSize: Int
    private val mMarkers = ArrayList<PageIndicatorMarker>()
    private var mActiveMarkerIndex = 0

    class PageMarkerResources {
        var activeId: Int
        var inactiveId: Int

        constructor() {
            activeId = R.drawable.ic_pageindicator_current
            inactiveId = R.drawable.ic_pageindicator_default
        }

        constructor(aId: Int, iaId: Int) {
            activeId = aId
            inactiveId = iaId
        }
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.PageIndicator, defStyle, 0
        )
        mMaxWindowSize = a.getInteger(R.styleable.PageIndicator_windowSize, 15)
        mWindowRange[0] = 0
        mWindowRange[1] = 0
        mLayoutInflater = LayoutInflater.from(context)
        a.recycle()

        // Set the layout transition properties
        val transition = layoutTransition
        transition.setDuration(175)
    }

    private fun enableLayoutTransitions() {
        val transition = layoutTransition
        transition.enableTransitionType(LayoutTransition.APPEARING)
        transition.enableTransitionType(LayoutTransition.DISAPPEARING)
        transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
    }

    private fun disableLayoutTransitions() {
        val transition = layoutTransition
        transition.disableTransitionType(LayoutTransition.APPEARING)
        transition.disableTransitionType(LayoutTransition.DISAPPEARING)
        transition.disableTransitionType(LayoutTransition.CHANGE_APPEARING)
        transition.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
    }

    private fun offsetWindowCenterTo(activeIndex: Int, allowAnimations: Boolean) {
        if (activeIndex < 0) {
            Throwable().printStackTrace()
        }
        val windowSize = min(mMarkers.size, mMaxWindowSize)
        val hWindowSize = windowSize / 2
        val hfWindowSize = windowSize / 2f
        var windowStart = max(0, activeIndex - hWindowSize)
        val windowEnd = min(mMarkers.size, windowStart + mMaxWindowSize)
        windowStart = windowEnd - min(mMarkers.size, windowSize)
        val windowMid = windowStart + (windowEnd - windowStart) / 2
        val windowAtStart = windowStart == 0
        val windowAtEnd = windowEnd == mMarkers.size
        val windowMoved = mWindowRange[0] != windowStart || mWindowRange[1] != windowEnd
        if (!allowAnimations) {
            disableLayoutTransitions()
        }

        // Remove all the previous children that are no longer in the window
        for (i in childCount - 1 downTo 0) {
            val marker = getChildAt(i) as PageIndicatorMarker
            val markerIndex = mMarkers.indexOf(marker)
            if (markerIndex < windowStart || markerIndex >= windowEnd) {
                removeView(marker)
            }
        }

        // Add all the new children that belong in the window
        for (i in mMarkers.indices) {
            val marker = mMarkers[i]
            if (i in windowStart until windowEnd) {
                if (indexOfChild(marker) < 0) {
                    addView(marker, i - windowStart)
                }
                if (i == activeIndex) {
                    marker.activate(windowMoved)
                } else {
                    marker.inactivate(windowMoved)
                }
            } else {
                marker.inactivate(true)
            }
            if (MODULATE_ALPHA_ENABLED) {
                // Update the marker's alpha
                var alpha = 1f
                if (mMarkers.size > windowSize) {
                    if (windowAtStart && i > hWindowSize || windowAtEnd && i < mMarkers.size - hWindowSize || !windowAtStart && !windowAtEnd) {
                        alpha = 1f - abs((i - windowMid) / hfWindowSize)
                    }
                }
                marker.animate().alpha(alpha).setDuration(500).start()
            }
        }
        if (!allowAnimations) {
            enableLayoutTransitions()
        }
        mWindowRange[0] = windowStart
        mWindowRange[1] = windowEnd
    }

    fun addMarker(index: Int, marker: PageMarkerResources, allowAnimations: Boolean) {
        var mIndex = index
        mIndex = max(0, min(mIndex, mMarkers.size))
        val m = mLayoutInflater.inflate(
            /* resource = */ R.layout.page_indicator_marker,
            /* root = */ this, /* attachToRoot = */ false
        ) as PageIndicatorMarker
        m.setMarkerDrawables(marker.activeId, marker.inactiveId)
        mMarkers.add(mIndex, m)
        offsetWindowCenterTo(mActiveMarkerIndex, allowAnimations)
    }

    fun addMarkers(markers: ArrayList<PageMarkerResources>, allowAnimations: Boolean) {
        for (i in markers.indices) {
            addMarker(Int.MAX_VALUE, markers[i], allowAnimations)
        }
    }

    fun updateMarker(index: Int, marker: PageMarkerResources) {
        val m = mMarkers[index]
        m.setMarkerDrawables(marker.activeId, marker.inactiveId)
    }

    fun removeMarker(index: Int, allowAnimations: Boolean) {
        var mIndex = index
        if (mMarkers.size > 0) {
            mIndex = max(0, min(mMarkers.size - 1, mIndex))
            mMarkers.removeAt(mIndex)
            offsetWindowCenterTo(mActiveMarkerIndex, allowAnimations)
        }
    }

    fun removeAllMarkers(allowAnimations: Boolean) {
        while (mMarkers.size > 0) {
            removeMarker(Int.MAX_VALUE, allowAnimations)
        }
    }

    fun setActiveMarker(index: Int) {
        // Center the active marker
        mActiveMarkerIndex = index
        offsetWindowCenterTo(index, false)
    }

    companion object {
        private const val MODULATE_ALPHA_ENABLED = false
    }
}
