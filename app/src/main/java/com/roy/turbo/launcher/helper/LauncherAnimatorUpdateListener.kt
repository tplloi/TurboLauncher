package com.roy.turbo.launcher.helper

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener

internal abstract class LauncherAnimatorUpdateListener : AnimatorUpdateListener {
    override fun onAnimationUpdate(animation: ValueAnimator) {
        val b = animation.animatedValue as Float
        val a = 1f - b
        onAnimationUpdate(a, b)
    }

    abstract fun onAnimationUpdate(a: Float, b: Float)
}
