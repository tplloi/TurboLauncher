package com.roy.turbo.launcher

import android.view.KeyEvent
import android.view.View

/**
 * A keyboard listener we set on all the workspace icons.
 */
internal class IconKeyEventListener : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        return FocusHelper.handleIconKeyEvent(v, keyCode, event)
    }
}
