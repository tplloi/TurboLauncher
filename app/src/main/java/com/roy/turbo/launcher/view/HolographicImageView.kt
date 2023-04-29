package com.roy.turbo.launcher.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ImageView
import com.roy.turbo.launcher.HolographicViewHelper
import com.roy.turbo.launcher.R

class HolographicImageView @SuppressLint("ClickableViewAccessibility") constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : ImageView(context, attrs, defStyle) {
    private val mHolographicHelper: HolographicViewHelper
    private var isHotwordOn: Boolean
    private var mIsPressed = false
    private var mIsFocused = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0) {
    }

    init {
        mHolographicHelper = HolographicViewHelper(context)
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.HolographicLinearLayout,
            defStyle, 0
        )
        isHotwordOn = a.getBoolean(R.styleable.HolographicLinearLayout_stateHotwordOn, false)
        a.recycle()
        setOnTouchListener { v: View?, event: MotionEvent? ->
            if (isPressed != mIsPressed) {
                mIsPressed = isPressed
                refreshDrawableState()
            }
            false
        }
        onFocusChangeListener = OnFocusChangeListener { _, _ ->
            if (isFocused != mIsFocused) {
                mIsFocused = isFocused
                refreshDrawableState()
            }
        }
    }

    fun invalidatePressedFocusedStates() {
        mHolographicHelper.invalidatePressedFocusedStates(this)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val d = drawable
        if (d is StateListDrawable) {
            val sld = d
            sld.state = drawableState
            sld.invalidateSelf()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // One time call to generate the pressed/focused state -- must be called after
        // measure/layout
        mHolographicHelper.generatePressedFocusedStates(this)
    }

    fun setHotwordState(on: Boolean) {
        if (on == isHotwordOn) {
            return
        }
        isHotwordOn = on
        refreshDrawableState()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isHotwordOn) {
            mergeDrawableStates(drawableState, intArrayOf(R.attr.stateHotwordOn))
        }
        return drawableState
    }
}
