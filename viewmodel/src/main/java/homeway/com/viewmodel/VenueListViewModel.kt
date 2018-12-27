package homeway.com.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import homeway.com.database.VenueDatabaseManager
import homeway.com.model.venue.Venue
import homeway.com.network.FourSquareManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VenueListViewModel @Inject constructor( private val venueDatabaseManager: VenueDatabaseManager,
                                              private val fourSquareManager: FourSquareManager) : BaseViewModel() {

    private val TAG = VenueListViewModel::class.java.simpleName

    fun getVenueListLiveData(): LiveData<List<Venue>> = venueListLiveData

    private val venueListLiveData = MutableLiveData<List<Venue>>()

    fun venueSearchTermUpdated( searchTerm: String ){
        disposables.clear()
        val disposable = fourSquareManager.getPlaces(searchTerm).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ venues ->
                    venueListLiveData.value = venues
                }, {
                    //TODO empty list
                    Log.e(TAG, "Error retrieving venues", it)
                })

        disposables.add(disposable)
    }

}