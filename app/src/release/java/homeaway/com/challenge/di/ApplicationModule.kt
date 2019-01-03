package homeway.com.challenge.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import homeway.com.network.NetworkModule
import homeway.com.viewmodel.di.ViewModelModule

@Module(includes = [ViewModelModule::class, ActivitiesModule::class, NetworkModule::class])
class ApplicationModule {

    @Provides
    fun providesContext(application: Application): Context = application.applicationContext
}