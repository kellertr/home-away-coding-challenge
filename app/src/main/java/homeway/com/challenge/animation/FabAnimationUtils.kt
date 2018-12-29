package homeway.com.challenge.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.view.ViewAnimationUtils
import android.animation.ArgbEvaluator
import android.content.Context
import android.view.View
import homeway.com.challenge.R


object FabAnimationUtils {
    fun registerCircularRevealAnimation(context: Context, view: View, revealSettings: RevealAnimationSetting) {
        val startColor = context.resources.getColor(R.color.colorPrimary)
        val endColor = context.resources.getColor(R.color.white)
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                val cx = revealSettings.centerX
                val cy = revealSettings.centerY
                val width = revealSettings.width
                val height = revealSettings.height
                val duration = context.resources.getInteger(android.R.integer.config_mediumAnimTime)

                //Simply use the diagonal of the view
                val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius).setDuration(duration.toLong())
                anim.interpolator = FastOutSlowInInterpolator()
                anim.start()
                startColorAnimation(view, startColor, endColor, duration)
            }
        })
    }

    fun startCircularExitAnimation( context:Context, view: View, revealSettings: RevealAnimationSetting, listener: Dismissable.OnDismissedListener ){

        val endColor = context.resources.getColor(R.color.colorPrimary)
        val startColor = context.resources.getColor(R.color.white)

        val cx = revealSettings.centerX
        val cy = revealSettings.centerY
        val width = revealSettings.width
        val height = revealSettings.height
        val duration = context.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()


        val initRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f).setDuration(duration)
        anim.duration = duration
        anim.interpolator = FastOutSlowInInterpolator ()

        anim.addListener( object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator){
                listener.onDismissed()
            }
        } )

        anim.start()
        startColorAnimation(view, startColor, endColor, duration.toInt())
    }

    internal fun startColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener { valueAnimator -> view.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.duration = duration.toLong()
        anim.start()
    }
}

interface Dismissable {
    interface OnDismissedListener {
        fun onDismissed()
    }

    fun dismiss(listener: OnDismissedListener)
}