package homeway.com.model.venue

/**
 * This data class contains values that are found in a Venue and Venue Detail calls. The values that
 * only show up in the venue detail api call are optional fields.
 */
data class Venue( val id: String,
                  val name: String,
                  val referralId: String,
                  val hasPerk: Boolean,
                  val canonicalUrl: String?,
                  val url: String?,
                  val location: VenueLocation,
                  val categories: List<VenueCategory>,
                  val contact: VenueContact?
                  )