package homeway.com.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.VenueSearchDisplay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VenueDetailViewModel @Inject constructor(val fourSquareManager: FourSquareManager): BaseViewModel(){

    val venueLiveData: MutableLiveData<VenueSearchDisplay> = MutableLiveData()

    var venue: VenueSearchDisplay? = null
        set(value) {
            value?.let {
                venueLiveData.value = value
                getVenueDetails( it )
            }
        }

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

    fun getVenueDetails( venueSearchDisplay: VenueSearchDisplay ){
        val disposable = fourSquareManager.getVenueDetails( venueSearchDisplay.id )
                .subscribeOn( Schedulers.io() ).observeOn( AndroidSchedulers.mainThread() ).map {
                    venueSearchDisplay.url = it.url
                    venueSearchDisplay
                }.subscribe({
                    venueLiveData.value = it
                }, {
                    //TODO error handling
                })

        disposables.add(disposable)
    }

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