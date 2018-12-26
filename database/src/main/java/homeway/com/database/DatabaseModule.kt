package homeway.com.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideVenueDatabase(context: Context): VenueDatabase {
        return Room.databaseBuilder(context, VenueDatabase::class.java, "favorites").build()
    }

    @Provides
    fun provideVenueDatabaseManager( venueDatabaseManager: VenueDatabaseManager ): VenueDatabaseManager = venueDatabaseManager

}