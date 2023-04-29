package com.roy.turbo.launcher

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

class DrawableStateProxyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    private var mView: View? = null
    private val mViewId: Int

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.DrawableStateProxyView, defStyle, 0
        )
        mViewId = a.getResourceId(R.styleable.DrawableStateProxyView_sourceViewId, -1)
        a.recycle()
        isFocusable = false
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mView == null) {
            val parent = parent as View
            mView = parent.findViewById(mViewId)
        }
        mView?.apply {
            isPressed = isPressed
            isHovered = isHovered
        }
    }

    override fun onHoverEvent(event: MotionEvent): Boolean {
        return false
    }
}
