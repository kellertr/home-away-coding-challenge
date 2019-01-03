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
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import java.util.concurrent.TimeUnit


/**
 * The BaseEspressoTest will serve as the base class for all Espresso test classes. We will establish
 * a mock webserver instance to mock all network traffic, a view idling resource so we can wait for
 * any action that happens asynchronously and a reference to the current activity that is being displayed
 */
abstract class BaseEspressoTest {
    private val TAG = BaseEspressoTest::class.java.simpleName

    private var currentActivity: WeakReference<Activity>? = null
    private var idlingResource: ViewIdlingResource? = null

    /**
     * This method will wait for a given ID to show up on the screen. Like any idling resource, it
     * will timeout if a given resource id is not found on the screen.
     *
     * @param resourceId is the resourceId we are waiting for
     */
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

    /**
     * This method will return the activity that is stored in the current activity variable or will set
     * the activity variable when the activity has been resumed
     *
     * @return the current activity that is shown on the screen
     */
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

    /**
     * @return the implementation of the Server Dispatcher from an implementing class
     */
    abstract fun getDispatcher(): Dispatcher

    @Before
    fun setup(){
        //We initialize the MockWebServer the first time if necessary to our mock port
        if( mockWebServer == null ) {
            mockWebServer = MockWebServer()
            mockWebServer?.start(FourSquareAPI.MOCK_PORT)
        }

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

        private var mockWebServer: MockWebServer? = null

        @BeforeClass
        fun beforeClass() {
            IdlingPolicies.setMasterPolicyTimeout(75, TimeUnit.SECONDS)
            IdlingPolicies.setIdlingResourceTimeout(75, TimeUnit.SECONDS)
        }

        @AfterClass
        fun afterClass() {
            mockWebServer?.shutdown()
        }
    }
}