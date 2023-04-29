package com.roy.turbo.launcher.sv

import android.os.Handler
import android.os.Looper
import com.roy.turbo.launcher.itf.OnAlarmListener
import kotlin.math.max

class Alarm : Runnable {
    private var mAlarmTriggerTime: Long = 0
    private var mWaitingForCallback = false
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private var mAlarmListener: OnAlarmListener? = null
    private var mAlarmPending = false

    fun setOnAlarmListener(alarmListener: OnAlarmListener?) {
        mAlarmListener = alarmListener
    }

    // Sets the alarm to go off in a certain number of milliseconds. If the alarm is already set,
    // it's overwritten and only the new alarm setting is used
    fun setAlarm(millisecondsInFuture: Long) {
        val currentTime = System.currentTimeMillis()
        mAlarmPending = true
        mAlarmTriggerTime = currentTime + millisecondsInFuture
        if (!mWaitingForCallback) {
            mHandler.postDelayed(this, mAlarmTriggerTime - currentTime)
            mWaitingForCallback = true
        }
    }

    fun cancelAlarm() {
        mAlarmTriggerTime = 0
        mAlarmPending = false
    }

    // this is called when our timer runs out
    override fun run() {
        mWaitingForCallback = false
        if (mAlarmTriggerTime != 0L) {
            val currentTime = System.currentTimeMillis()
            if (mAlarmTriggerTime > currentTime) {
                // We still need to wait some time to trigger spring loaded mode--
                // post a new callback
                mHandler.postDelayed(
                    /* r = */ this,
                    /* delayMillis = */max(0, mAlarmTriggerTime - currentTime)
                )
                mWaitingForCallback = true
            } else {
                mAlarmPending = false
                mAlarmListener?.onAlarm(this)
            }
        }
    }

    fun alarmPending(): Boolean {
        return mAlarmPending
    }
}
