package homeway.com.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The VenueDatabase represents the abstract layer that we will utilize to build and utilize a room database
 */
@Database(entities = [VenueEntry::class], version = 1)
abstract class VenueDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
}