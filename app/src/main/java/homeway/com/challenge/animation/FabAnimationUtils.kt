package homeway.com.challenge.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.view.ViewAnimationUtils
import android.animation.ArgbEvaluator
import android.view.View
import homeway.com.challenge.R

/**
 * The FabAnimationUtils class will handle the circular reveal and collapse of the FAB
 */
object FabAnimationUtils {

    /**
     * This method will handle the circular reveal animation of a given view. From this view, the view will explode outwards until it encompasses
     * the entire screen
     *
     * @param view is the view we are animating
     * @param revealSettings are the view settings we are using to animate
     */
    fun registerCircularRevealAnimation(view: View, revealSettings: RevealAnimationSetting) {
        val startColor = view.resources.getColor(R.color.colorPrimary)
        val endColor = view.resources.getColor(R.color.white)

        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                val duration = v.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()

                val anim = ViewAnimationUtils.createCircularReveal(v, revealSettings.centerX,
                        revealSettings.centerY, 0f, revealSettings.calculateRadius()).setDuration(duration)

                anim.interpolator = FastOutSlowInInterpolator()
                anim.start()
                startColorAnimation(view, startColor,
                        endColor, duration)
            }
        })
    }

    /**
     * This method will exit a given view down into a collapsed view. This method has the exact
     * opposite effect of registerCircularRevealAnimation()
     *
     * @param view is the view which we are animating
     * @param revealSettings are the view settings we are using to animate down against
     * @param listener is the interface we will utilize when the view has completed animating
     */
    fun startCircularExitAnimation(view: View, revealSettings: RevealAnimationSetting, listener: Dismissable.OnDismissedListener ){
        val duration = view.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()

        val anim = ViewAnimationUtils.createCircularReveal(view, revealSettings.centerX,
                revealSettings.centerY, revealSettings.calculateRadius(), 0f).setDuration(duration)
        anim.duration = duration
        anim.interpolator = FastOutSlowInInterpolator ()

        anim.addListener( object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator){
                listener.onDismissed()
            }
        } )

        anim.start()
        startColorAnimation(view, view.resources.getColor(R.color.white), view.resources.getColor(R.color.colorPrimary), duration)
    }

    /**
     * This is a helper method that will trigger a color animation on a given view
     *
     * @param view is the view that will be experiencing the color animation
     * @param startColor is the color to sue at the start of the animation
     * @param endColor is the desired end color of the animation
     * @param duration is the time it will take to complete this animation
     */
    private fun startColorAnimation(view: View, startColor: Int, endColor: Int, duration: Long) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener { valueAnimator -> view.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.duration = duration
        anim.start()
    }
}

/**
 * The Dismissable interface will be utilized in conjunction with FabAnimationUtils. This interface will
 * be used by calling classes to begin exit animations
 */
interface Dismissable {

    /**
     * The OnDismissedListener will be used in conjunction with Dismissable
     */
    interface OnDismissedListener {

        /**
         * The method onDismissed is meant to be invoked when a class has finished animating
         */
        fun onDismissed()
    }

    /**
     * This method will begin an exit animation on a given view
     * @param listener will be invoked when the animation is complete
     */
    fun dismiss(listener: OnDismissedListener)
}