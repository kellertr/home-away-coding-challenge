package homeway.com.challenge.view

import homeway.com.viewmodel.model.DisplayVenue

/**
 * This interface will extend the VenueFavoritedInterface.
 *
 * It will alert an implementing class that a venue row was clicked
 */
interface VenueRowInterface : VenueFavoritedInterface{
    fun onRowClicked(displayVenue: DisplayVenue)
}

/**
 * This interface is utilized to inform an implementing class that an item has been favorited
 *
 */
interface VenueFavoritedInterface {

    /**
     * This method is called with a venue that was just favorited
     *
     * @param displayVenue is the venue that has been clicked from a given position
     * @param position is the position in the list this venue was clicked at
     */
    fun venueFavoriteAdjusted(displayVenue: DisplayVenue, position: Int)
}