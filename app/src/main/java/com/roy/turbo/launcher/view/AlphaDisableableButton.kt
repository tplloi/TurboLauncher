package com.roy.turbo.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Button

/**
 * A Button which becomes translucent when it is disabled
 */
class AlphaDisableableButton @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    companion object {
        var DISABLED_ALPHA_VALUE = 0.4f
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) {
            1.0f
        } else {
            DISABLED_ALPHA_VALUE
        }
    }
}
