package com.roy.turbo.launcher;

import android.os.Handler;

public class Alarm implements Runnable {

    private long mAlarmTriggerTime;

    private boolean mWaitingForCallback;

    private final Handler mHandler;
    private OnAlarmListener mAlarmListener;
    private boolean mAlarmPending = false;

    public Alarm() {
        mHandler = new Handler();
    }

    public void setOnAlarmListener(OnAlarmListener alarmListener) {
        mAlarmListener = alarmListener;
    }

    // Sets the alarm to go off in a certain number of milliseconds. If the alarm is already set,
    // it's overwritten and only the new alarm setting is used
    public void setAlarm(long millisecondsInFuture) {
        long currentTime = System.currentTimeMillis();
        mAlarmPending = true;
        mAlarmTriggerTime = currentTime + millisecondsInFuture;
        if (!mWaitingForCallback) {
            mHandler.postDelayed(this, mAlarmTriggerTime - currentTime);
            mWaitingForCallback = true;
        }
    }

    public void cancelAlarm() {
        mAlarmTriggerTime = 0;
        mAlarmPending = false;
    }

    // this is called when our timer runs out
    public void run() {
        mWaitingForCallback = false;
        if (mAlarmTriggerTime != 0) {
            long currentTime = System.currentTimeMillis();
            if (mAlarmTriggerTime > currentTime) {
                // We still need to wait some time to trigger spring loaded mode--
                // post a new callback
                mHandler.postDelayed(this, Math.max(0, mAlarmTriggerTime - currentTime));
                mWaitingForCallback = true;
            } else {
                mAlarmPending = false;
                if (mAlarmListener != null) {
                    mAlarmListener.onAlarm(this);
                }
            }
        }
    }

    public boolean alarmPending() {
        return mAlarmPending;
    }
}
