package homeway.com.database

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single


/**
 * The Venue Dao is the interface that Room utilizes to build a Room interface
 */
@Dao
interface VenueDao {
    /**
     * This method queries the VenueDatabase to find if a given list of venue ids are among the
     * favorite venues for a user. It will return a Single that will emit a list of VenueEntries
     * as stored in the VenueDatabase that can be utilized by the calling class.
     *
     * @param venueIds is a list of venue ids we will query the database to retrieve favorite entries
     * @return a single that will emit a list of Venue Entries
     */
    @Query("SELECT * FROM favorites where venueId IN (:venueIds)")
    fun favoriteVenues( venueIds: List<String> ): Observable<List<VenueEntry>>

    /**
     * This method will get a favorite venue from the VenueDatabase for a given venueID.
     *
     * @param venueId is the venue id for the current venue
     * @return a Single that will emit a VenueEntry, or null if not found
     */
    @Query("SELECT * FROM favorites where venueId=:venueId")
    fun getFavoriteVenue( venueId: String ): Single<VenueEntry>

    /**
     * This method will insert a venue into the VenueDatabase. If a venue already exists for the given
     * VenueId, we will replace it. The venue ids are unique so this scenario should never be encountered.
     *
     * @param venueEntry is the venue entry we are adding to the VenueDatabase
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(venueEntry: VenueEntry)

    /**
     * This method will remove a venue from the VenueDatabase.
     *
     * @param venueEntry is the venue entry we are removing from the VenueDatabase
     */
    @Delete
    fun remove(venueEntry: VenueEntry)
}

@Entity(tableName = "favorites")
data class VenueEntry(@PrimaryKey val venueId: String)