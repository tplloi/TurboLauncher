package com.roy.turbo.launcher.view

import android.view.KeyEvent
import android.view.View
import com.roy.turbo.launcher.FocusHelper

/**
 * A keyboard listener we set on all the workspace icons.
 */
internal class FolderKeyEventListener : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        return FocusHelper.handleFolderKeyEvent(v, keyCode, event)
    }
}
