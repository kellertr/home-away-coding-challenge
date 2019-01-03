package homeway.com.challenge.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import homeaway.com.network.MockNetworkModule
import homeway.com.viewmodel.di.ViewModelModule

@Module(includes = [ViewModelModule::class, ActivitiesModule::class, MockNetworkModule::class])
class ApplicationModule {

    @Provides
    fun providesContext(application: Application): Context = application.applicationContext
}