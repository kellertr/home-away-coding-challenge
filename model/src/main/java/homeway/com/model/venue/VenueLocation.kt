package homeway.com.model.venue

/**
 * The location associated to a given venue from the FourSquare API
 */
data class VenueLocation(val address: String,
                         val crossStreet: String,
                         val lat: Double,
                         val lng: Double,
                         val distance: Int,
                         val postalCode: String,
                         val city: String,
                         val state: String,
                         val country: String,
                         val formattedAddress: List<String>)