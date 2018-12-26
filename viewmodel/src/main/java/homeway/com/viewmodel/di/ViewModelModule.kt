package homeway.com.viewmodel.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import homeway.com.database.DatabaseModule
import homeway.com.network.NetworkModule

@Module(includes = [NetworkModule::class, DatabaseModule::class])
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactoryModule(factory: ViewModelFactory): ViewModelProvider.Factory
}