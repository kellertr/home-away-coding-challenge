package homeway.com.network

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideFourSquareAPI(): FourSquareAPI = FourSquareAPI.create()

    @Provides
    fun provideFourSquareManager( fourSquareManager: FourSquareManager ): FourSquareManager = fourSquareManager
}