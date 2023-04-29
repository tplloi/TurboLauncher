package com.roy.turbo.launcher

import android.view.KeyEvent
import android.view.View

/**
 * A keyboard listener we set on all the workspace icons.
 */
internal class FolderKeyEventListener : View.OnKeyListener {
    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        return FocusHelper.handleFolderKeyEvent(v, keyCode, event)
    }
}
