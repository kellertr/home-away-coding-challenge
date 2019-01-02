package homeway.com.viewmodel.model

/**
 * The display venue class is a data class that will be utilizes by the UI to display venues. We
 * will simplify the implementation and only provide core components of the Venue class to the calling
 * classes.
 */
class DisplayVenue(var name: String = "",
                   var distance: Int? = null,
                   var id: String = "",
                   var category: String? = null,
                   var favorite: Boolean = false,
                   var latitude: Double = 0.0,
                   var longitude: Double = 0.0,
                   var url: String? = null,
                   var categoryIconUrl: String? = null)