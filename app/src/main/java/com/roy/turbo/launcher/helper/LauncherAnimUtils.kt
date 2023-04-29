package com.roy.turbo.launcher.helper

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewTreeObserver.OnDrawListener
import java.util.WeakHashMap

object LauncherAnimUtils {
    var sAnimators = WeakHashMap<Animator, Any?>()
    var sEndAnimListener: AnimatorListener = object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            sAnimators[animation] = null
        }

        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            sAnimators.remove(animation)
        }

        override fun onAnimationCancel(animation: Animator) {
            sAnimators.remove(animation)
        }
    }

    @JvmStatic
    fun cancelOnDestroyActivity(a: Animator) {
        a.addListener(sEndAnimListener)
    }

    // Helper method. Assumes a draw is pending, and that if the animation's duration is 0
    // it should be cancelled
    @JvmStatic
    fun startAnimationAfterNextDraw(animator: Animator, view: View) {
        view.viewTreeObserver.addOnDrawListener(object : OnDrawListener {
            private var mStarted = false
            override fun onDraw() {
                if (mStarted) return
                mStarted = true
                // Use this as a signal that the animation was cancelled
                if (animator.duration == 0L) {
                    return
                }
                animator.start()
                val listener: OnDrawListener = this
                view.post { view.viewTreeObserver.removeOnDrawListener(listener) }
            }
        })
    }

    @JvmStatic
    fun onDestroyActivity() {
        val animators = HashSet(sAnimators.keys)
        for (a in animators) {
            if (a.isRunning) {
                a.cancel()
            }
            sAnimators.remove(a)
        }
    }

    @JvmStatic
    fun createAnimatorSet(): AnimatorSet {
        val anim = AnimatorSet()
        cancelOnDestroyActivity(anim)
        return anim
    }

    @JvmStatic
    fun ofFloat(target: View?, vararg values: Float): ValueAnimator {
        val anim = ValueAnimator()
        anim.setFloatValues(*values)
        cancelOnDestroyActivity(anim)
        return anim
    }

    @JvmStatic
    fun ofFloat(target: View?, propertyName: String?, vararg values: Float): ObjectAnimator {
        val anim = ObjectAnimator()
        anim.target = target
        propertyName?.let {
            anim.setPropertyName(it)
        }
        anim.setFloatValues(*values)
        cancelOnDestroyActivity(anim)
        FirstFrameAnimatorHelper(anim, target)
        return anim
    }

    @JvmStatic
    fun ofPropertyValuesHolder(
        target: View?,
        vararg values: PropertyValuesHolder?
    ): ObjectAnimator {
        val anim = ObjectAnimator()
        anim.target = target
        anim.setValues(*values)
        cancelOnDestroyActivity(anim)
        FirstFrameAnimatorHelper(anim, target)
        return anim
    }

    @JvmStatic
    fun ofPropertyValuesHolder(
        target: Any?,
        view: View?,
        vararg values: PropertyValuesHolder?
    ): ObjectAnimator {
        val anim = ObjectAnimator()
        anim.target = target
        anim.setValues(*values)
        cancelOnDestroyActivity(anim)
        FirstFrameAnimatorHelper(anim, view)
        return anim
    }
}
