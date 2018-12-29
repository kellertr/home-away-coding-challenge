package homeway.com.challenge.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import homeway.com.challenge.VenueActivity
import homeway.com.challenge.fragment.VenueMapListFragment
import homeway.com.challenge.fragment.VenueSearchFragment


@Suppress("unused")
@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): VenueActivity

    @ContributesAndroidInjector
    abstract fun contributeVenueSearchFragment(): VenueSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeVenueMapListFragment(): VenueMapListFragment

}