package homeway.com.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.picasso.Picasso
import homeway.com.database.VenueDatabaseManager
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.DisplayVenue
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * The VenueListViewModel will contain all business logic that is utilized when displaying the Venue
 * List Screen
 */
class VenueListViewModel @Inject constructor(private val venueDatabaseManager: VenueDatabaseManager,
                                             private val fourSquareManager: FourSquareManager) : BaseViewModel() {

    private val TAG = VenueListViewModel::class.java.simpleName
    private val ICON_IMAGE_SIZE = 512

    fun getVenueListLiveData(): LiveData<List<DisplayVenue>> = venueListLiveData
    fun getVenueModifiedLiveData(): LiveData<Pair<Int, DisplayVenue>> = venueModifiedLiveData

    private val venueListLiveData = MutableLiveData<List<DisplayVenue>>()
    private val venueModifiedLiveData = MutableLiveData<Pair<Int, DisplayVenue>>()

    /**
     * This method will interact with both the four square api and the database containing favorited
     * venues. First, we will attempt to load the venues from the four square api using the user
     * provided search term. Upon successfully receiving venues, we will do a database lookup
     * utilizing the venue ids from the venue database. Upon receiving these favorited venues, we will
     * merge the results of the venue search and favorited lookup to create venues that will be utilized
     * by the front end for display.
     *
     * @param searchTerm is the search term that the user is searching for
     */
    fun venueSearchTermUpdated(searchTerm: String, mapsApiKey: String, width: Int, height: Int) {
        disposables.clear()
        venueModifiedLiveData.value = null

        val disposable = fourSquareManager.getPlaces(searchTerm).toObservable().subscribeOn(Schedulers.io())
                .flatMap({ venues ->
                    venueDatabaseManager.favoriteVenues(venues.map { it.id })
                }, { venues, favoriteVenueIds ->
                    venues.map { venue ->

                        val category = if (venue.categories.isNotEmpty()) venue.categories[0] else null

                        val displayVenue = DisplayVenue(name = venue.name,
                                distance = venue.location.distance,
                                id = venue.id,
                                category = category?.name,
                                favorite = favoriteVenueIds.contains(venue.id),
                                latitude = venue.location.lat,
                                longitude = venue.location.lng
                                )

                        displayVenue.googleMapsUrl = getGoogleMapsUrl( mapsApiKey, width,
                                height, displayVenue )

                        //Attempt to load the google map for the detail page here and cache it in the
                        //Picasso image cache
                        Picasso.get().load(displayVenue.googleMapsUrl).fetch()

                        category?.let {
                            displayVenue.categoryIconUrl = "${it.icon.prefix}$ICON_IMAGE_SIZE${it.icon.suffix}"
                        }

                        displayVenue
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ venues ->
                    venueListLiveData.value = venues
                }, {
                    //TODO empty list
                    Log.e(TAG, "Error retrieving venues", it)
                })

        disposables.add(disposable)
    }

    /**
     * This method will mark a given venue as a favorite venue. If the venue is already a favorite
     * venue, we will delete this venue from the favorites database. Upon success, we will update the
     * live data with the new venue and the position it was updated from.
     *
     * @param venue the venue that we are either favoriting or not favoriting
     * @param position is the position in the list the venue was favorited from
     */
    fun venueFavorited(venue: DisplayVenue, position: Int) {
        disposables.add(Completable.fromCallable {
            venue.favorite = venue.favorite.not()

            if (venue.favorite) {
                venueDatabaseManager.insert(venue.id)
            } else {
                venueDatabaseManager.remove(venue.id)
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                    venueModifiedLiveData.value = Pair(position, venue)
                    venueModifiedLiveData.value = null
                }.subscribe())
    }

    /**
     * This method will construct a Google Maps Static Map URL utilizing the venue from the live data
     * object
     *
     * @param key is the key parameter we ae passing on to Google
     * @param width is the width of the image we are requesting from Google
     * @param height is the height of the image we are requesting from Google
     */
    fun getGoogleMapsUrl(key: String, width: Int, height: Int, venue: DisplayVenue) =
                         Uri.Builder()
                        .scheme(HTTPS_SCHEME)
                        .authority(MAPS_BASE_URL)
                        .path(MAPS_PATH)
                        .appendQueryParameter(QUERY_PARAM_ZOOM, ZOOM)
                        .appendQueryParameter(QUERY_PARAM_SCALE, SCALE)
                        .appendQueryParameter(QUERY_PARAM_MARKERS, "$MARKER_COLOR|${venue.latitude},${venue.longitude}")
                        .appendQueryParameter(QUERY_PARAM_MARKERS, "$MARKER_COLOR|$MARKER_LABEL$SEATTLE|$SEATTLE_LAT_LONG")
                        .appendQueryParameter(QUERY_PARAM_SIZE, "${width}x${height}")
                        .appendQueryParameter(QUERY_PARAM_KEY, key).build().toString()

    /**
     * The companion object will house string constants utilized by the VenueDetailViewModel class. These
     * variables are utilized to build a Google Maps Static Maps URL.
     */
    companion object {
        private const val HTTPS_SCHEME = "https"
        private const val MAPS_BASE_URL = "maps.googleapis.com"
        private const val MAPS_PATH = "maps/api/staticmap"

        private const val QUERY_PARAM_ZOOM = "zoom"
        private const val QUERY_PARAM_MARKERS = "markers"
        private const val QUERY_PARAM_KEY = "key"
        private const val QUERY_PARAM_SCALE = "scale"
        private const val QUERY_PARAM_SIZE = "size"

        private const val SCALE = "2"
        private const val ZOOM = "15"
        private const val MARKER_COLOR = "color:red"
        private const val MARKER_LABEL = "label:"
        private const val SEATTLE = "Seattle"
        private const val SEATTLE_LAT_LONG = "47.60621,-122.33207"
    }

}