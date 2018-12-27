package homeway.com.challenge.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import homeway.com.viewmodel.di.ViewModelModule

@Module(includes = [ViewModelModule::class])
class ApplicationModule {

    @Provides
    fun providesContext(application: Application): Context = application.applicationContext
}