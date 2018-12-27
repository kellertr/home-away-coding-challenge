package homeway.com.challenge.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import homeway.com.challenge.MainActivity
import homeway.com.challenge.VenueSearchFragment


@Suppress("unused")
@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeVenueSearchFragment(): VenueSearchFragment

}