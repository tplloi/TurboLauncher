package com.roy.turbo.launcher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import kotlin.math.max
import kotlin.math.min

/**
 * A convenience class for two-way animations, e.g. a fadeIn/fadeOut animation.
 * With a regular ValueAnimator, if you call reverse to show the 'out' animation, you'll get
 * a frame-by-frame mirror of the 'in' animation -- i.e., the interpolated values will
 * be exactly reversed. Using this class, both the 'in' and the 'out' animation use the
 * interpolator in the same direction.
 */
class InterruptibleInOutAnimator(view: View?, duration: Long, fromValue: Float, toValue: Float) {
    private val mOriginalDuration: Long
    private val mOriginalFromValue: Float
    private val mOriginalToValue: Float
    val animator: ValueAnimator
    private var mFirstRun = true
    var tag: Any? = null

    // TODO: This isn't really necessary, but is here to help diagnose a bug in the drag viz
    private var mDirection = STOPPED

    init {
        animator = LauncherAnimUtils.ofFloat(view, fromValue, toValue).setDuration(duration)
        mOriginalDuration = duration
        mOriginalFromValue = fromValue
        mOriginalToValue = toValue
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mDirection = STOPPED
            }
        })
    }

    private fun animate(direction: Int) {
        val currentPlayTime = animator.currentPlayTime
        val toValue = if (direction == IN) mOriginalToValue else mOriginalFromValue
        val startValue = if (mFirstRun) mOriginalFromValue else (animator.animatedValue as Float)

        // Make sure it's stopped before we modify any values
        cancel()

        // TODO: We don't really need to do the animation if startValue == toValue, but
        // somehow that doesn't seem to work, possibly a quirk of the animation framework
        mDirection = direction

        // Ensure we don't calculate a non-sensical duration
        val duration = mOriginalDuration - currentPlayTime
        animator.duration = max(0, min(duration, mOriginalDuration))
        animator.setFloatValues(startValue, toValue)
        animator.start()
        mFirstRun = false
    }

    fun cancel() {
        animator.cancel()
        mDirection = STOPPED
    }

    fun end() {
        animator.end()
        mDirection = STOPPED
    }

    /**
     * Return true when the animation is not running and it hasn't even been started.
     */
    val isStopped: Boolean
        get() = mDirection == STOPPED

    /**
     * This is the equivalent of calling Animator.start(), except that it can be called when
     * the animation is running in the opposite direction, in which case we reverse
     * direction and animate for a correspondingly shorter duration.
     */
    fun animateIn() {
        animate(IN)
    }

    /**
     * This is the roughly the equivalent of calling Animator.reverse(), except that it uses the
     * same interpolation curve as animateIn(), rather than mirroring it. Also, like animateIn(),
     * if the animation is currently running in the opposite direction, we reverse
     * direction and animate for a correspondingly shorter duration.
     */
    fun animateOut() {
        animate(OUT)
    }

    companion object {
        private const val STOPPED = 0
        private const val IN = 1
        private const val OUT = 2
    }
}
