package homeaway.com.network

import dagger.Module
import dagger.Provides
import homeway.com.network.FourSquareAPI
import javax.inject.Singleton

/**
 * The MockNetworkModule provides an instance of the FourSquareAPI that is pointed to localhost. This
 * is utilized by the Espresso tests
 */
@Module
class MockNetworkModule {

    @Provides
    @Singleton
    fun provideFourSquareAPI(): FourSquareAPI = FourSquareAPI.create(FourSquareAPI.MOCK_URL)
}