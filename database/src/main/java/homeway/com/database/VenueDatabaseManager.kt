package homeway.com.database

import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/**
 * The VenueDatabaseManager class handles any business logic over the VenueDao. Essentially, this class
 * is intended to make any calling class have to implement less to retrieve what they need with respect
 * to a favorite Venue.
 */
class VenueDatabaseManager @Inject constructor(private val venueDao: VenueDao){

    /**
     * This method will retrieve favorite venue ids from a list of string venue ids
     *
     * @param venueIds is a list of Venue ids that we would like to validate whether or not they are favorited or not
     * @return a single that will emit a list of venue ids that correspond to the lookup of any favorited venues
     */
    fun favoriteVenues( venueIds: List<String> ): Observable<List<String>> {
        return venueDao.favoriteVenues(venueIds).map { dbEntries ->

            val favoriteIds = ArrayList<String>()

            for( entry in dbEntries ){
                favoriteIds.add(entry.venueId)
            }

            favoriteIds
        }
    }

    /**
     * This method will insert a venue into the VenueDatabase by VenueID
     *
     * @param venueId is the venue which we are inserting into the Venue Database
     */
    fun insert(venueId: String){
        venueDao.insert(VenueEntry(venueId))
    }

    /**
     * This method will remove a venue from the venue database.
     *
     * @param venueId is the venueId for the venue that we are removing from the VenueDatabase
     */
    fun remove(venueId: String){
        venueDao.remove(VenueEntry(venueId))
    }

}