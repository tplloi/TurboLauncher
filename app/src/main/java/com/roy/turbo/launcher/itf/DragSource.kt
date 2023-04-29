package com.roy.turbo.launcher.itf

import android.view.View
import com.roy.turbo.launcher.DropTarget.DragObject

/**
 * Interface defining an object that can originate a drag.
 *
 */
interface DragSource {
    /**
     * @return whether items dragged from this source supports
     */
    fun supportsFlingToDelete(): Boolean

    /**
     * @return whether items dragged from this source supports 'App Info'
     */
    fun supportsAppInfoDropTarget(): Boolean

    /**
     * @return whether items dragged from this source supports 'Delete' drop target (e.g. to remove
     * a shortcut.
     */
    fun supportsDeleteDropTarget(): Boolean

    /*
     * @return the scale of the icons over the workspace icon size
     */
    val intrinsicIconScaleFactor: Float

    /**
     * A callback specifically made back to the source after an item from this source has been flung
     * to be deleted on a DropTarget.  In such a situation, this method will be called after
     * onDropCompleted, and more importantly, after the fling animation has completed.
     */
    fun onFlingToDeleteCompleted()

    /**
     * A callback made back to the source after an item from this source has been dropped on a
     * DropTarget.
     */
    fun onDropCompleted(target: View?, d: DragObject?, isFlingToDelete: Boolean, success: Boolean)
}
