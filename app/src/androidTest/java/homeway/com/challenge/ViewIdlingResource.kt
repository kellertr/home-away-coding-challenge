package homeway.com.challenge

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.test.espresso.IdlingResource

class ViewIdlingResource(val currentActivity: Activity, private val viewResourceId: Int) : IdlingResource {
    private var idleNow: Boolean = false
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = TAG

    override fun isIdleNow(): Boolean{
            if (idleNow) {
                return true
            }

            if (viewResourceId <= 0) {
                throw AssertionError("Invalid resource id")
            }

            if (isViewAvailable) {
                Log.d(TAG, "ViewIdlingResource is idle")
                resourceCallback?.onTransitionToIdle()
                idleNow = true
                return true
            }
            return false
        }


    private val isViewAvailable: Boolean
        get() {
            val resourceView = currentActivity.findViewById<View?>(viewResourceId)

            return resourceView?.visibility == View.VISIBLE &&
                    resourceView.width > 0 && resourceView.height > 0
        }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.resourceCallback = callback
    }

    companion object {
        private val TAG = ViewIdlingResource::class.java.simpleName
    }
}
