package homeway.com.network

import homeway.com.model.venue.Venue
import io.reactivex.Single
import java.lang.Exception
import java.util.*
import javax.inject.Inject

/**
 * This class was written to handle any additional logic that is needed when utilizing the
 * FourSquareAPI. Fortunately, the only thing that needs to be done to these classes is to unwrap
 * the responses to avoid providing less than valuable responses to the calling classes.
 */
class FourSquareManager @Inject constructor(private val fourSquareAPI: FourSquareAPI){

    /**
     * This method will get a list of places from a given search term. It will interact with the
     * FourSqareAPI and strip the response to only give calling classes the information they desire.
     *
     * @param query is the search term we will be passing to the foursquare api
     * @return a single that will handle interaction with the foursquare api for a venue search
     */
    fun getPlaces( query: String ) : Single<List<Venue>> {
        return fourSquareAPI.getPlaces(searchTerm = query).map {
            it.response?.venues ?: Collections.emptyList()
        }
    }

    /**
     *  This method will get details for a given place based on the venue's id. It will provide calling
     *  classes with only the information about a venue and no additional information.
     *
     * @param venueId is the venue id we will utilize to perform a venue lookup from foursquare
     * @return a single that will handle interaction with the foursquare api for a venue detail lookup
     */
    fun getVenueDetails( venueId: String ) : Single<Venue> {
        return fourSquareAPI.getVenueDetails( venueId ).map {
            if( it == null || it.response == null || it.response.venue == null ){
                throw EmptyVenueException()
            }

            it.response.venue
        }
    }
}

/*
 * These exceptions are utilized when we are unable to find any more information on a given search
 * term or venue
 */
class EmptyVenueException : Exception()


