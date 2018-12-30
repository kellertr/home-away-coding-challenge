package homeway.com.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.VenueSearchDisplay
import javax.inject.Inject

class VenueDetailViewModel @Inject constructor(val fourSquareManager: FourSquareManager): BaseViewModel(){

    var venue: VenueSearchDisplay? = null

    fun getGoogleMapsUrl(key: String, width: Int, height: Int) =
            venue?.let {
                val builder = Uri.Builder()
                        .scheme(HTTPS_SCHEME)
                        .authority(MAPS_BASE_URL)
                        .path(MAPS_PATH)
                        .appendQueryParameter(QUERY_PARAM_ZOOM, ZOOM)
                        .appendQueryParameter(QUERY_PARAM_SCALE, SCALE)
                        .appendQueryParameter(QUERY_PARAM_MARKERS, "$MARKER_COLOR|${it.latitude},${it.longitude}")
                        .appendQueryParameter(QUERY_PARAM_SIZE, "${width}x${height}")
                        .appendQueryParameter(QUERY_PARAM_KEY, key)

                builder.build().toString()
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
    }
}