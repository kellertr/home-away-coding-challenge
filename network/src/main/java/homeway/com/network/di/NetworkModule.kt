package homeway.com.network.di

import dagger.Module
import dagger.Provides
import homeway.com.network.FourSquareAPI
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideFourSquareAPI(): FourSquareAPI = FourSquareAPI.create()
}