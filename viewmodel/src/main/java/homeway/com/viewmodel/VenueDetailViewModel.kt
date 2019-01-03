package homeway.com.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.DisplayVenue
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * The VenueDetailViewModel will contain all business logic that is utilized when displaying the Venue
 * Detail Screen
 */
class VenueDetailViewModel @Inject constructor(val fourSquareManager: FourSquareManager): BaseViewModel(){

    val venueLiveData: MutableLiveData<DisplayVenue> = MutableLiveData()

    /**
     * This parameter is used in conjunction with the shared view model. Upon setting this value, we
     * will call the four square api to load more details about this venue.
     */
    var venue: DisplayVenue? = null
        set(value) {
            value?.let {
                venueLiveData.value = value
                getVenueDetails( it )
            }
        }

    /**
     * This method will get appropriate details for a given venue from the foursquare API and it will
     * update the venue live data object for all calling classes.
     *
     * @param displayVenue is the venue we are getting details for
     */
    fun getVenueDetails(displayVenue: DisplayVenue ){
        val disposable = fourSquareManager.getVenueDetails( displayVenue.id )
                .subscribeOn( Schedulers.io() ).observeOn( AndroidSchedulers.mainThread() ).map {
                    displayVenue.url = it.url
                    displayVenue
                }.subscribe({
                    venueLiveData.value = it
                }, {
                    //TODO error handling
                })

        disposables.add(disposable)
    }
}