package homeway.com.challenge

import android.app.Activity
import android.app.Application
import android.util.Log
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import homeway.com.challenge.di.AppInjector
import javax.inject.Inject
import homeway.com.challenge.di.DaggerApplicationComponent
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

class ChallengeApplication : Application(), HasActivityInjector {

    val TAG = ChallengeApplication::class.java.simpleName

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return androidInjector
    }

    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        AppInjector.init(this)

        RxJavaPlugins.setErrorHandler {
            var throwable = it
                if (it is UndeliverableException) {
                    throwable = throwable.cause
                }

                Log.e(TAG, "Handled unhandled RXJava network error From" +
                        " a disposable that has already been disposed", throwable)
        }
    }


}