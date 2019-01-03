package homeway.com.challenge.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

import dagger.android.support.AndroidSupportInjectionModule
import homeway.com.challenge.ChallengeApplication
import homeway.com.network.NetworkModule

/**
 * Dagger Component needed to build the Dagger Graph for required dependencies
 */
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApplicationModule::class])
interface ApplicationComponent {

    fun inject(app: ChallengeApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}