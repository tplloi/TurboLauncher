package com.roy.turbo.launcher.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import com.roy.turbo.launcher.FastBitmapDrawable
import com.roy.turbo.launcher.R

class HolographicViewHelper(context: Context) {
    private val mTempCanvas = Canvas()
    private var mStatesUpdated = false
    private val mHighlightColor: Int
    private val mHotwordColor: Int

    init {
        val res = context.resources
        mHighlightColor = res.getColor(android.R.color.holo_blue_light)
        mHotwordColor = res.getColor(android.R.color.holo_green_light)
    }

    /**
     * Generate the pressed/focused states if necessary.
     */
    fun generatePressedFocusedStates(v: ImageView?) {
        if (!mStatesUpdated && v != null) {
            mStatesUpdated = true
            val original = createOriginalImage(v, mTempCanvas)
            val outline = createImageWithOverlay(v, mTempCanvas, mHighlightColor)
            val hotword = createImageWithOverlay(v, mTempCanvas, mHotwordColor)
            val originalD = FastBitmapDrawable(original)
            val outlineD = FastBitmapDrawable(outline)
            val hotwordD = FastBitmapDrawable(hotword)
            val states = StateListDrawable()
            states.addState(intArrayOf(android.R.attr.state_pressed), outlineD)
            states.addState(intArrayOf(android.R.attr.state_focused), outlineD)
            states.addState(intArrayOf(R.attr.stateHotwordOn), hotwordD)
            states.addState(intArrayOf(), originalD)
            v.setImageDrawable(states)
        }
    }

    /**
     * Invalidates the pressed/focused states.
     */
    fun invalidatePressedFocusedStates(v: ImageView?) {
        mStatesUpdated = false
        v?.invalidate()
    }

    /**
     * Creates a copy of the original image.
     */
    private fun createOriginalImage(v: ImageView, canvas: Canvas): Bitmap {
        val d = v.drawable
        val b = Bitmap.createBitmap(
            d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(b)
        canvas.save()
        d.draw(canvas)
        canvas.restore()
        canvas.setBitmap(null)
        return b
    }

    /**
     * Creates a new press state image which is the old image with a blue overlay.
     * Responsibility for the bitmap is transferred to the caller.
     */
    private fun createImageWithOverlay(v: ImageView, canvas: Canvas, color: Int): Bitmap {
        val d = v.drawable
        val b = Bitmap.createBitmap(
            d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(b)
        canvas.save()
        d.draw(canvas)
        canvas.restore()
        canvas.drawColor(color, PorterDuff.Mode.SRC_IN)
        canvas.setBitmap(null)
        return b
    }
}
