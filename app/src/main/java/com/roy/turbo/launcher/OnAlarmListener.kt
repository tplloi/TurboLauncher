package com.roy.turbo.launcher

import com.roy.turbo.launcher.sv.Alarm

interface OnAlarmListener {
    fun onAlarm(alarm: Alarm?)
}
