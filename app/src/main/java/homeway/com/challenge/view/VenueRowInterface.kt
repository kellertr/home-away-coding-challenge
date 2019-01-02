package homeway.com.challenge.view

import homeway.com.viewmodel.model.DisplayVenue

interface VenueRowInterface {
    fun onFavoriteClicked(displayVenue: DisplayVenue, position: Int)
    fun onRowClicked(displayVenue: DisplayVenue)
}