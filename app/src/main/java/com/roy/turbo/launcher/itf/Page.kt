package com.roy.turbo.launcher.itf

import android.view.View

interface Page {
    val pageChildCount: Int
    fun getChildOnPageAt(i: Int): View?
    fun removeAllViewsOnPage()
    fun removeViewOnPageAt(i: Int)
    fun indexOfChildOnPage(v: View?): Int
}
