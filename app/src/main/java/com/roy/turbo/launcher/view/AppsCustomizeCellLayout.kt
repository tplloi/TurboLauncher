package com.roy.turbo.launcher.view

import android.content.Context
import android.view.View
import com.roy.turbo.launcher.CellLayout

class AppsCustomizeCellLayout(context: Context) : CellLayout(context), Page {
    override fun removeAllViewsOnPage() {
        removeAllViews()
        setLayerType(LAYER_TYPE_NONE, null)
    }

    override fun removeViewOnPageAt(i: Int) {
        removeViewAt(i)
    }

    override val pageChildCount: Int
        get() = childCount

    override fun getChildOnPageAt(i: Int): View? {
        return getChildAt(i)
    }

    override fun indexOfChildOnPage(v: View?): Int {
        return indexOfChild(v)
    }

//    fun resetChildrenOnKeyListeners() {
//        val children = shortcutsAndWidgets
//        val childCount = children.childCount
//        for (j in 0 until childCount) {
//            children.getChildAt(j).setOnKeyListener(null)
//        }
//    }
}
