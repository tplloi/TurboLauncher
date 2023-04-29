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
import android.widget.LinearLayout
import com.roy.turbo.launcher.HolographicViewHelper
import com.roy.turbo.launcher.R

class HolographicLinearLayout @SuppressLint("ClickableViewAccessibility") constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : LinearLayout(context, attrs, defStyle) {
    private val mHolographicHelper: HolographicViewHelper
    private var mImageView: ImageView? = null
    private val mImageViewId: Int
    private var isHotwordOn: Boolean
    private var mIsPressed = false
    private var mIsFocused = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.HolographicLinearLayout,
            defStyle, 0
        )
        mImageViewId = a.getResourceId(R.styleable.HolographicLinearLayout_sourceImageViewId, -1)
        isHotwordOn = a.getBoolean(R.styleable.HolographicLinearLayout_stateHotwordOn, false)
        a.recycle()
        setWillNotDraw(false)
        mHolographicHelper = HolographicViewHelper(context)
        setOnTouchListener { _: View?, _: MotionEvent? ->
            if (isPressed != mIsPressed) {
                mIsPressed = isPressed
                refreshDrawableState()
            }
            false
        }
        onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            if (isFocused != mIsFocused) {
                mIsFocused = isFocused
                refreshDrawableState()
            }
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        mImageView?.let {
            val d = it.drawable
            if (d is StateListDrawable) {
                d.state = drawableState
                d.invalidateSelf()
            }
        }
    }

    fun invalidatePressedFocusedStates() {
        mHolographicHelper.invalidatePressedFocusedStates(mImageView)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // One time call to generate the pressed/focused state -- must be called after
        // measure/layout
        if (mImageView == null) {
            mImageView = findViewById<View>(mImageViewId) as ImageView
        }
        mHolographicHelper.generatePressedFocusedStates(mImageView)
    }

    fun setHotwordState(on: Boolean) {
        if (on == isHotwordOn) {
            return
        }
        isHotwordOn = on
        refreshDrawableState()
    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isHotwordOn) {
            mergeDrawableStates(drawableState, intArrayOf(R.attr.stateHotwordOn))
        }
        return drawableState
    }
}