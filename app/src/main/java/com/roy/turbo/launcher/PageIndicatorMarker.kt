package com.roy.turbo.launcher

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

class PageIndicatorMarker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(
    context, attrs, defStyle
) {
    private var mActiveMarker: ImageView? = null
    private var mInactiveMarker: ImageView? = null
    private var isActive = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        mActiveMarker = findViewById<View>(R.id.active) as ImageView
        mInactiveMarker = findViewById<View>(R.id.inactive) as ImageView
    }

    fun setMarkerDrawables(activeResId: Int, inactiveResId: Int) {
        val r = resources
        mActiveMarker?.setImageDrawable(r.getDrawable(activeResId))
        mInactiveMarker?.setImageDrawable(r.getDrawable(inactiveResId))
    }

    fun activate(immediate: Boolean) {
        if (immediate) {
            mActiveMarker?.apply {
                animate().cancel()
                alpha = 1f
                scaleX = 1f
                scaleY = 1f
            }
            mInactiveMarker?.apply {
                animate().cancel()
                alpha = 0f
            }
        } else {
            mActiveMarker?.animate()?.alpha(1f)?.scaleX(1f)?.scaleY(1f)
                ?.setDuration(MARKER_FADE_DURATION.toLong())?.start()

            mInactiveMarker?.animate()?.alpha(0f)?.setDuration(MARKER_FADE_DURATION.toLong())
                ?.start()
        }
        isActive = true
    }

    fun inactivate(immediate: Boolean) {
        if (immediate) {
            mInactiveMarker?.apply {
                animate().cancel()
                alpha = 1f
            }
            mActiveMarker?.apply {
                animate().cancel()
                alpha = 0f
                scaleX = 0.5f
                scaleY = 0.5f
            }
        } else {
            mInactiveMarker?.animate()?.alpha(1f)?.setDuration(MARKER_FADE_DURATION.toLong())
                ?.start()
            mActiveMarker?.animate()?.alpha(0f)?.scaleX(0.5f)?.scaleY(0.5f)
                ?.setDuration(MARKER_FADE_DURATION.toLong())?.start()
        }
        isActive = false
    }

    companion object {
        private const val MARKER_FADE_DURATION = 175
    }
}
