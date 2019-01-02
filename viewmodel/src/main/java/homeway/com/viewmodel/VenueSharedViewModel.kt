package homeway.com.viewmodel

import androidx.lifecycle.MutableLiveData
import homeway.com.viewmodel.model.DisplayVenue
import javax.inject.Inject

/**
 * The VenueSharedViewModel will be utilied to store relevant data that is to be shared across
 * screens. We will share a list of venues and the selected venue.
 */
class VenueSharedViewModel @Inject constructor() : BaseViewModel() {

    val venues: MutableLiveData<List<DisplayVenue>> = MutableLiveData()
    val selectedVenue: MutableLiveData<DisplayVenue> = MutableLiveData()
}