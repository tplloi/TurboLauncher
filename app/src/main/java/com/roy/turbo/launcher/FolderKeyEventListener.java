package com.roy.turbo.launcher;

import android.view.KeyEvent;
import android.view.View;

/**
 * A keyboard listener we set on all the workspace icons.
 */
class FolderKeyEventListener implements View.OnKeyListener {
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleFolderKeyEvent(v, keyCode, event);
    }
}