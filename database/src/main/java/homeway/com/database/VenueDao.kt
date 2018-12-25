package homeway.com.database

import androidx.room.*
import io.reactivex.Single


@Dao
interface VenueDao {
    @Query("SELECT * FROM favorites where venueId IN (:venueIds)")
    fun favoriteVenues( venueIds: List<String> ): Single<List<VenueEntry>>

    @Query("SELECT * FROM favorites where venueId=:venueId")
    fun getFavoriteVenue( venueId: String ): Single<VenueEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(venueId: VenueEntry)

    @Delete
    fun remove(venueId: VenueEntry)
}

@Entity(tableName = "favorites")
data class VenueEntry(@PrimaryKey val venueId: String)