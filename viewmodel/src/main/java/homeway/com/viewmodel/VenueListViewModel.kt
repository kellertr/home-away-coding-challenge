package homeway.com.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import homeway.com.database.VenueDatabaseManager
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.DisplayVenue
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * The VenueListViewModel will contain all business logic that is utilized when displaying the Venue
 * List Screen
 */
class VenueListViewModel @Inject constructor(private val venueDatabaseManager: VenueDatabaseManager,
                                             private val fourSquareManager: FourSquareManager) : BaseViewModel() {

    private val TAG = VenueListViewModel::class.java.simpleName
    private val ICON_IMAGE_SIZE = 512

    fun getVenueListLiveData(): LiveData<List<DisplayVenue>> = venueListLiveData
    fun getVenueModifiedLiveData(): LiveData<Pair<Int, DisplayVenue>> = venueModifiedLiveData

    private val venueListLiveData = MutableLiveData<List<DisplayVenue>>()
    private val venueModifiedLiveData = MutableLiveData<Pair<Int, DisplayVenue>>()

    /**
     * This method will interact with both the four square api and the database containing favorited
     * venues. First, we will attempt to load the venues from the four square api using the user
     * provided search term. Upon successfully receiving venues, we will do a database lookup
     * utilizing the venue ids from the venue database. Upon receiving these favorited venues, we will
     * merge the results of the venue search and favorited lookup to create venues that will be utilized
     * by the front end for display.
     *
     * @param searchTerm is the search term that the user is searching for
     */
    fun venueSearchTermUpdated(searchTerm: String) {
        disposables.clear()
        venueModifiedLiveData.value = null

        val disposable = fourSquareManager.getPlaces(searchTerm).toObservable().subscribeOn(Schedulers.io())
                .flatMap({ venues ->
                    venueDatabaseManager.favoriteVenues(venues.map { it.id })
                }, { venues, favoriteVenueIds ->
                    venues.map { venue ->

                        val category = if (venue.categories.isNotEmpty()) venue.categories[0] else null

                        val displayVenue = DisplayVenue(name = venue.name,
                                distance = venue.location.distance,
                                id = venue.id,
                                category = category?.name,
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

    /**
     * This method will mark a given venue as a favorite venue. If the venue is already a favorite
     * venue, we will delete this venue from the favorites database. Upon success, we will update the
     * live data with the new venue and the position it was updated from.
     *
     * @param venue the venue that we are either favoriting or not favoriting
     * @param position is the position in the list the venue was favorited from
     */
    fun venueFavorited(venue: DisplayVenue, position: Int) {
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