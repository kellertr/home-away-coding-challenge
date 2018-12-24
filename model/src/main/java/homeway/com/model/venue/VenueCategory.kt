package homeway.com.model.venue

/**
 * This class is a model class for mappings of a venue category returned from the four square api.
 */
data class VenueCategory(val id: String,
                         val name: String,
                         val pluralName: String,
                         val shortName: String,
                         val primary: Boolean,
                         val icon: VenueIcon){

    /**
     * An easier way to get the icon url rather than returning two strings or the whole object for a
     * VenueIcon
     */
    val iconUrl = "${icon.prefix}${icon.suffix}"
}

/**
 * A class that maps to a service response from the FourSquare API for icons associated to a venue
 */
data class VenueIcon(val prefix: String, val suffix: String)