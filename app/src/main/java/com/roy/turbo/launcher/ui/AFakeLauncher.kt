package com.roy.turbo.launcher.ui

import android.app.Activity
import android.os.Bundle
import com.roy.turbo.launcher.R

class AFakeLauncher : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_fake_launcher)
    }
}