package homeway.com.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VenueEntry::class], version = 1)
abstract class VenueDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
}