package com.roy.turbo.launcher

import android.view.KeyEvent
import android.view.View

/**
 * A keyboard listener we set on all the hotseat buttons.
 */
internal class HotseatIconKeyEventListener : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        val configuration = v.resources.configuration
        return FocusHelper.handleHotseatButtonKeyEvent(v, keyCode, event, configuration.orientation)
    }
}
