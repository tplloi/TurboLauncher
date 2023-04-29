package com.roy.turbo.launcher.view

import android.view.KeyEvent
import android.view.View
import com.roy.turbo.launcher.FocusHelper

/**
 * A keyboard listener we set on all the hotseat buttons.
 */
internal class HotseatIconKeyEventListener : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        val configuration = v.resources.configuration
        return FocusHelper.handleHotseatButtonKeyEvent(v, keyCode, event, configuration.orientation)
    }
}
