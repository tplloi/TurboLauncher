package com.roy.turbo.launcher

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView

//done 2023.03.29
internal class PagedViewWidgetImageView(context: Context?, attrs: AttributeSet?) :
    ImageView(context, attrs) {
    @JvmField
    var mAllowRequestLayout = true
    override fun requestLayout() {
        if (mAllowRequestLayout) {
            super.requestLayout()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(
            /* left = */ scrollX + paddingLeft,
            /* top = */ scrollY + paddingTop,
            /* right = */ scrollX + right - left - paddingRight,
            /* bottom = */ scrollY + bottom - top - paddingBottom
        )
        super.onDraw(canvas)
        canvas.restore()
    }
}
