package homeway.com.challenge.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

import dagger.android.support.AndroidSupportInjectionModule
import homeway.com.challenge.ChallengeApplication

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApplicationModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: ChallengeApplication)
}