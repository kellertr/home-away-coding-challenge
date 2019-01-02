package homeway.com.viewmodel

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler

/**
 * The BaseViewModel Test will include the setup and teardown functionality of the rx test scheduler
 * that is utilized by the implementing testing classes.
 */
open class BaseViewModelTest {

    private lateinit var testScheduler: TestScheduler

    open fun setup(){
        testScheduler = TestScheduler()
        RxJavaPlugins.setInitComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setInitIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setInitSingleSchedulerHandler { testScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
    }

    fun teardown(){
        RxJavaPlugins.setInitComputationSchedulerHandler { null }
        RxJavaPlugins.setInitIoSchedulerHandler { null }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { null }
        RxJavaPlugins.setInitSingleSchedulerHandler { null }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { null }
    }

    /**
     * Advance the scheduler to the next test action
     */
    fun advanceScheduler(){
        testScheduler.triggerActions()
    }
}