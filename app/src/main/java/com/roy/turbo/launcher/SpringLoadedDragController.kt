package com.roy.turbo.launcher

import com.roy.turbo.launcher.itf.OnAlarmListener
import com.roy.turbo.launcher.sv.Alarm

class SpringLoadedDragController(private val mLauncher: Launcher) : OnAlarmListener {
    // how long the user must hover over a mini-screen before it unshrinks
    private val ENTER_SPRING_LOAD_HOVER_TIME: Long = 500
    private val ENTER_SPRING_LOAD_CANCEL_HOVER_TIME: Long = 950
    var mAlarm: Alarm = Alarm()

    // the screen the user is currently hovering over, if any
    private var mScreen: CellLayout? = null

    init {
        mAlarm.setOnAlarmListener(this)
    }

    fun cancel() {
        mAlarm.cancelAlarm()
    }

    // Set a new alarm to expire for the screen that we are hovering over now
    fun setAlarm(cl: CellLayout?) {
        mAlarm.cancelAlarm()
        mAlarm.setAlarm(if (cl == null) ENTER_SPRING_LOAD_CANCEL_HOVER_TIME else ENTER_SPRING_LOAD_HOVER_TIME)
        mScreen = cl
    }

    // this is called when our timer runs out
    override fun onAlarm(alarm: Alarm?) {
        if (mScreen != null) {
            // Snap to the screen that we are hovering over now
            val w = mLauncher.workspace
            val page = w.indexOfChild(mScreen)
            if (page != w.currentPage) {
                w.snapToPage(page)
            }
        } else {
            mLauncher.dragController.cancelDrag()
        }
    }
}
