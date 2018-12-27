package homeway.com.viewmodel.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import homeway.com.database.DatabaseModule
import homeway.com.network.NetworkModule
import homeway.com.viewmodel.VenueDetailViewModel
import homeway.com.viewmodel.VenueListViewModel
import homeway.com.viewmodel.VenueSharedViewModel

@Module(includes = [NetworkModule::class, DatabaseModule::class])
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactoryModule(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(VenueListViewModel::class)
    abstract fun bindVenueListViewModel(viewModel: VenueListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VenueDetailViewModel::class)
    abstract fun bindVenueDetailViewModel(viewModel: VenueDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VenueSharedViewModel::class)
    abstract fun bindVenueSharedViewModel(viewModel: VenueSharedViewModel): ViewModel
}