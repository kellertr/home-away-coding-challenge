package homeway.com.challenge.view

import homeway.com.viewmodel.model.VenueSearchDisplay

interface VenueRowInterface {
    fun onFavoriteClicked(venueSearchDisplay: VenueSearchDisplay, position: Int)
    fun onRowClicked(venueSearchDisplay: VenueSearchDisplay)
}