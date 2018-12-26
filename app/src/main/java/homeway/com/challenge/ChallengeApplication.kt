package homeway.com.challenge

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject
import homeway.com.challenge.di.DaggerApplicationComponent

class ChallengeApplication : Application(), HasActivityInjector {

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
    }


}