package com.roy.turbo.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import com.roy.turbo.launcher.Folder

class FolderEditText : EditText {
    private var mFolder: Folder? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun setFolder(folder: Folder?) {
        mFolder = folder
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        // Catch the back button on the soft keyboard so that we can just close the activity
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            mFolder?.doneEditingFolderName(true)
        }
        return super.onKeyPreIme(keyCode, event)
    }
}
