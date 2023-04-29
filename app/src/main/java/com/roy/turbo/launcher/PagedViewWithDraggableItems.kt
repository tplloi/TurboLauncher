package com.roy.turbo.launcher

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import kotlin.math.abs

//done 2023.04.29
abstract class PagedViewWithDraggableItems @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : PagedView(context, attrs, defStyle), OnLongClickListener, OnTouchListener {
    private var mLastTouchedItem: View? = null
    private var mIsDragging = false
    private var mIsDragEnabled = false
    private var mDragSlopeThreshold = 0f
    private val mLauncher: Launcher

    init {
        mLauncher = context as Launcher
    }

    protected open fun beginDragging(v: View?): Boolean {
        val wasDragging = mIsDragging
        mIsDragging = true
        return !wasDragging
    }

    private fun cancelDragging() {
        mIsDragging = false
        mLastTouchedItem = null
        mIsDragEnabled = false
    }

    private fun handleTouchEvent(ev: MotionEvent) {
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                cancelDragging()
                mIsDragEnabled = true
            }

            MotionEvent.ACTION_MOVE -> if (mTouchState != TOUCH_STATE_SCROLLING && !mIsDragging && mIsDragEnabled) {
                determineDraggingStart(ev)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        handleTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        handleTouchEvent(ev)
        return super.onTouchEvent(ev)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mLastTouchedItem = v
        mIsDragEnabled = true
        return false
    }

    override fun onLongClick(v: View): Boolean {
        // Return early if this is not initiated from a touch
        if (!v.isInTouchMode) return false
        // Return early if we are still animating the pages
        if (mNextPage != INVALID_PAGE) return false
        // When we have exited all apps or are in transition, disregard long clicks
        if (!mLauncher.isAllAppsVisible ||
            mLauncher.workspace.isSwitchingState
        ) return false
        // Return if global dragging is not enabled
        return if (!mLauncher.isDraggingEnabled) false else beginDragging(v)
    }

    override fun determineScrollingStart(ev: MotionEvent) {
        if (!mIsDragging) super.determineScrollingStart(ev)
    }

    protected open fun determineDraggingStart(ev: MotionEvent) {
        val pointerIndex = ev.findPointerIndex(mActivePointerId)
        val x = ev.getX(pointerIndex)
        val y = ev.getY(pointerIndex)
        val xDiff = abs(x - mLastMotionX).toInt()
        val yDiff = abs(y - mLastMotionY).toInt()
        val touchSlop = mTouchSlop
        val yMoved = yDiff > touchSlop
        val isUpwardMotion = yDiff / xDiff.toFloat() > mDragSlopeThreshold
        if (isUpwardMotion && yMoved && mLastTouchedItem != null) {
            // Drag if the user moved far enough along the Y axis
            beginDragging(mLastTouchedItem)

            // Cancel any pending long press
            if (mAllowLongPress) {
                mAllowLongPress = false
                // Try canceling the long press. It could also have been scheduled
                // by a distant descendant, so use the mAllowLongPress flag to block
                // everything
                val currentPage = getPageAt(mCurrentPage)
                currentPage?.cancelLongPress()
            }
        }
    }

    fun setDragSlopeThreshold(dragSlopeThreshold: Float) {
        mDragSlopeThreshold = dragSlopeThreshold
    }

    override fun onDetachedFromWindow() {
        cancelDragging()
        super.onDetachedFromWindow()
    }
}
