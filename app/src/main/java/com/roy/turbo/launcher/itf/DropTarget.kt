package com.roy.turbo.launcher.itf

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import com.roy.turbo.launcher.DragController
import com.roy.turbo.launcher.Launcher
import com.roy.turbo.launcher.view.DragView

interface DropTarget {
    class DragObject {
        @JvmField
        var x = -1

        @JvmField
        var y = -1

        /** X offset from the upper-left corner of the cell to where we touched.  */
        @JvmField
        var xOffset = -1

        /** Y offset from the upper-left corner of the cell to where we touched.  */
        @JvmField
        var yOffset = -1

        /**
         * This indicates whether a drag is in final stages, either drop or
         * cancel. It differentiates onDragExit, since this is called when the
         * drag is ending, above the current drag target, or when the drag moves
         * off the current drag object.
         */
        @JvmField
        var dragComplete = false

        /** The view that moves around while you drag.  */
        @JvmField
        var dragView: DragView? = null

        /** The data associated with the object being dragged  */
        @JvmField
        var dragInfo: Any? = null

        /** Where the drag originated  */
        @JvmField
        var dragSource: DragSource? = null

        /** Post drag animation runnable  */
        @JvmField
        var postAnimationRunnable: Runnable? = null

        /** Indicates that the drag operation was cancelled  */
        @JvmField
        var cancelled = false

        /**
         * Defers removing the DragView from the DragLayer until after the drop
         * animation.
         */
        @JvmField
        var deferDragViewCleanupPostAnimation = true
    }

    class DragEnforcer(context: Context) : DragController.DragListener {
        private var dragParity = 0

        init {
            val launcher = context as Launcher
            launcher.dragController.addDragListener(this)
        }

        fun onDragEnter() {
            dragParity++
        }

        fun onDragExit() {
            dragParity--
        }

        override fun onDragStart(source: DragSource, info: Any, dragAction: Int) {}
        override fun onDragEnd() {}
    }

    /**
     * Used to temporarily disable certain drop targets
     *
     * @return boolean specifying whether this drop target is currently enabled
     */
    val isDropEnabled: Boolean
    fun onDrop(dragObject: DragObject?)
    fun onDragEnter(dragObject: DragObject?)
    fun onDragOver(dragObject: DragObject?)
    fun onDragExit(dragObject: DragObject?)

    /**
     * Handle an object being dropped as a result of flinging to delete and will
     * be called in place of onDrop(). (This is only called on objects that are
     * set as the DragController's fling-to-delete target.
     */
    fun onFlingToDelete(dragObject: DragObject?, x: Int, y: Int, vec: PointF?)
    fun acceptDrop(dragObject: DragObject?): Boolean

    // These methods are implemented in Views
    fun getHitRectRelativeToDragLayer(outRect: Rect?)
    fun getLocationInDragLayer(loc: IntArray?)
    val left: Int
    val top: Int
}
