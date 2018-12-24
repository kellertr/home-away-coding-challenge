package homeway.com.model.venue

/**
 * The class that represents a contact for a venue that is returned from the FourSquareAPI
 */
data class VenueContact(val phone: String,
                        val formattedPhone: String,
                        val twitter: String)