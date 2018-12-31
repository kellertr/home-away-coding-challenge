package homeway.com.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import homeway.com.database.VenueDatabaseManager
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.VenueSearchDisplay
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VenueListViewModel @Inject constructor(private val venueDatabaseManager: VenueDatabaseManager,
                                             private val fourSquareManager: FourSquareManager) : BaseViewModel() {

    private val TAG = VenueListViewModel::class.java.simpleName
    private val ICON_IMAGE_SIZE = 512

    fun getVenueListLiveData(): LiveData<List<VenueSearchDisplay>> = venueListLiveData
    fun getVenueModifiedLiveData(): LiveData<Pair<Int, VenueSearchDisplay>> = venueModifiedLiveData

    private val venueListLiveData = MutableLiveData<List<VenueSearchDisplay>>()
    private val venueModifiedLiveData = MutableLiveData<Pair<Int, VenueSearchDisplay>>()

    fun venueSearchTermUpdated(searchTerm: String) {
        disposables.clear()
        venueModifiedLiveData.value = null

        val disposable = fourSquareManager.getPlaces(searchTerm).toObservable().subscribeOn(Schedulers.io())
                .flatMap({ venues ->
                    venueDatabaseManager.favoriteVenues(venues.map { it.id })
                }, { venues, favoriteVenueIds ->
                    venues.map { venue ->

                        val category = if (venue.categories.isNotEmpty()) venue.categories[0] else null

                        val displayVenue = VenueSearchDisplay(name = venue.name,
                                distance = venue.location.distance,
                                id = venue.id,
                                favorite = favoriteVenueIds.contains(venue.id),
                                latitude = venue.location.lat,
                                longitude = venue.location.lng
                                )

                        category?.let {
                            displayVenue.categoryIconUrl = "${it.icon.prefix}$ICON_IMAGE_SIZE${it.icon.suffix}"
                        }

                        displayVenue
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ venues ->
                    venueListLiveData.value = venues
                }, {
                    //TODO empty list
                    Log.e(TAG, "Error retrieving venues", it)
                })

        disposables.add(disposable)
    }

    fun venueFavorited(venue: VenueSearchDisplay, position: Int) {
        disposables.add(Completable.fromCallable {
            venue.favorite = venue.favorite.not()

            if (venue.favorite) {
                venueDatabaseManager.insert(venue.id)
            } else {
                venueDatabaseManager.remove(venue.id)
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                    venueModifiedLiveData.value = Pair(position, venue)
                    venueModifiedLiveData.value = null
                }.subscribe())
    }

}