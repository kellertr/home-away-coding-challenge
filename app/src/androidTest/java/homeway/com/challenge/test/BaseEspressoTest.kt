package homeway.com.challenge.test

import android.app.Activity
import android.util.Log
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import java.lang.ref.WeakReference
import androidx.test.runner.lifecycle.Stage.RESUMED
import homeway.com.network.FourSquareAPI
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import java.util.concurrent.TimeUnit


abstract class BaseEspressoTest {
    private val TAG = BaseEspressoTest::class.java.simpleName

    private var currentActivity: WeakReference<Activity>? = null
    private var idlingResource: ViewIdlingResource? = null
    private var mockWebServer: MockWebServer? = null

    fun waitForId( resourceId: Int ){
        idlingResource?.let {
            IdlingRegistry.getInstance().unregister(it)
            idlingResource = null
        }
        getCurrentActivity()?.let {
            idlingResource = ViewIdlingResource(it, resourceId)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    protected fun getCurrentActivity(): Activity? {
        Log.d(TAG, "getCurrentActivity")

        currentActivity?.get()?.let {
            return it
        } ?: run {
            getInstrumentation().runOnMainSync{
                val allActivityList = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED)
                if (allActivityList.iterator().hasNext()) {
                    currentActivity = WeakReference(Iterables.getOnlyElement(allActivityList))
                }
            }
        }

        return currentActivity?.get()
    }

    abstract fun getDispatcher(): Dispatcher

    @Before
    fun setup(){

        mockWebServer?.shutdown()
        mockWebServer = MockWebServer()
        mockWebServer?.start(FourSquareAPI.MOCK_PORT)

        mockWebServer?.setDispatcher(getDispatcher())

        getInstrumentation().waitForIdleSync()
        getCurrentActivity()
    }

    @After
    fun teardown() {

        idlingResource.let {
            IdlingRegistry.getInstance().unregister(idlingResource)
            idlingResource = null
        }

        currentActivity = null
    }

    companion object {
        @BeforeClass
        fun beforeClass() {
            IdlingPolicies.setMasterPolicyTimeout(75, TimeUnit.SECONDS)
            IdlingPolicies.setIdlingResourceTimeout(75, TimeUnit.SECONDS)
        }
    }
}