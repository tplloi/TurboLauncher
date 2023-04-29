package com.roy.turbo.launcher.itf

import com.roy.turbo.launcher.sv.Alarm

interface OnAlarmListener {
    fun onAlarm(alarm: Alarm?)
}
