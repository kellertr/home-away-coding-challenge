package homeway.com.viewmodel

import androidx.lifecycle.MutableLiveData
import homeway.com.viewmodel.model.VenueSearchDisplay
import javax.inject.Inject

class VenueSharedViewModel @Inject constructor() : BaseViewModel() {

    val venues: MutableLiveData<List<VenueSearchDisplay>> = MutableLiveData()
    val selectedVenue: MutableLiveData<VenueSearchDisplay> = MutableLiveData()
}