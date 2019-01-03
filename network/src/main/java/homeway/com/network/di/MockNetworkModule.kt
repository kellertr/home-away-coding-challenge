package homeaway.com.network

import dagger.Module
import dagger.Provides
import homeway.com.network.FourSquareAPI
import javax.inject.Singleton

@Module
class MockNetworkModule {

    @Provides
    @Singleton
    fun provideFourSquareAPI(): FourSquareAPI = FourSquareAPI.create(FourSquareAPI.MOCK_URL)
}