package com.roy.turbo.launcher.view

import android.view.KeyEvent
import android.view.View
import com.roy.turbo.launcher.FocusHelper

/**
 * A keyboard listener we set on all the workspace icons.
 */
internal class IconKeyEventListener : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        return FocusHelper.handleIconKeyEvent(v, keyCode, event)
    }
}
