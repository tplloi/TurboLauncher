package com.roy.turbo.launcher

interface DragScroller {
    fun scrollLeft()
    fun scrollRight()

    /**
     * The touch point has entered the scroll area; a scroll is imminent.
     * This event will only occur while a drag is active.
     *
     * @param direction The scroll direction
     */
    fun onEnterScrollArea(x: Int, y: Int, direction: Int): Boolean

    /**
     * The touch point has left the scroll area.
     * NOTE: This may not be called, if a drop occurs inside the scroll area.
     */
    fun onExitScrollArea()
}
