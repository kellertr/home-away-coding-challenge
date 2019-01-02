package homeway.com.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.DisplayVenue
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * The VenueDetailViewModel will contain all business logic that is utilized when displaying the Venue
 * Detail Screen
 */
class VenueDetailViewModel @Inject constructor(val fourSquareManager: FourSquareManager): BaseViewModel(){

    val venueLiveData: MutableLiveData<DisplayVenue> = MutableLiveData()

    /**
     * This parameter is used in conjunction with the shared view model. Upon setting this value, we
     * will call the four square api to load more details about this venue.
     */
    var venue: DisplayVenue? = null
        set(value) {
            value?.let {
                venueLiveData.value = value
                getVenueDetails( it )
            }
        }

    /**
     * This method will construct a Google Maps Static Map URL utilizing the venue from the live data
     * object
     *
     * @param key is the key parameter we ae passing on to Google
     * @param width is the width of the image we are requesting from Google
     * @param height is the height of the image we are requesting from Google
     */
    fun getGoogleMapsUrl(key: String, width: Int, height: Int) =
            venueLiveData.value?.let {
                val builder = Uri.Builder()
                        .scheme(HTTPS_SCHEME)
                        .authority(MAPS_BASE_URL)
                        .path(MAPS_PATH)
                        .appendQueryParameter(QUERY_PARAM_ZOOM, ZOOM)
                        .appendQueryParameter(QUERY_PARAM_SCALE, SCALE)
                        .appendQueryParameter(QUERY_PARAM_MARKERS, "$MARKER_COLOR|${it.latitude},${it.longitude}")
                        .appendQueryParameter(QUERY_PARAM_MARKERS, "$MARKER_COLOR|$MARKER_LABEL$SEATTLE|$SEATTLE_LAT_LONG")
                        .appendQueryParameter(QUERY_PARAM_SIZE, "${width}x${height}")
                        .appendQueryParameter(QUERY_PARAM_KEY, key)

                builder.build().toString()
            }

    /**
     * This method will get appropriate details for a given venue from the foursquare API and it will
     * update the venue live data object for all calling classes.
     *
     * @param displayVenue is the venue we are getting details for
     */
    fun getVenueDetails(displayVenue: DisplayVenue ){
        val disposable = fourSquareManager.getVenueDetails( displayVenue.id )
                .subscribeOn( Schedulers.io() ).observeOn( AndroidSchedulers.mainThread() ).map {
                    displayVenue.url = it.url
                    displayVenue
                }.subscribe({
                    venueLiveData.value = it
                }, {
                    //TODO error handling
                })

        disposables.add(disposable)
    }

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