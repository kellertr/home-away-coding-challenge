package homeway.com.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import homeway.com.database.VenueDatabaseManager
import homeway.com.model.venue.Venue
import homeway.com.model.venue.VenueCategory
import homeway.com.model.venue.VenueIcon
import homeway.com.model.venue.VenueLocation
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.DisplayVenue
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.*
import org.junit.runner.RunWith
import java.util.*


/**
 * This is a simple test of the VenueListViewModel. We will mock the implementations of VenueDatabaseManager and
 * FourSquareManager since we are only concerned with the implementation of VenueListViewModel in this
 * test class.
 */
@RunWith(AndroidJUnit4::class)
class VenueListViewModelTest: BaseViewModelTest() {

    private val venueDBManager: VenueDatabaseManager = mock()
    private val fourSquareManager: FourSquareManager = mock()

    private lateinit var venueListViewModel: VenueListViewModel

    private val SEARCH_TERM = "search"
    private val VENUE_ID = "venueId"
    private val LAT = 25.0
    private val LONG = 50.0

    @Before
    override fun setup() {
        super.setup()

        venueListViewModel = VenueListViewModel( venueDBManager, fourSquareManager )
    }

    @After
    fun after() {
        teardown()
    }

    @Test
    fun favoritesSearchTest() {

        super.setup()

        whenever( venueDBManager.favoriteVenues(any()) ).thenReturn( Observable.just( listOf(VENUE_ID) ) )
        whenever(fourSquareManager.getPlaces(SEARCH_TERM)).thenReturn(Single.just(buildVenueList()))

        venueListViewModel.venueSearchTermUpdated(SEARCH_TERM, "", 0, 0)
        advanceScheduler()

        val searchResults = venueListViewModel.getVenueListLiveData().value

        val firstResult = searchResults?.get(0)
        val secondResult = searchResults?.get(1)

        Assert.assertNotNull("SearchResults are null", searchResults)
        Assert.assertNotNull("SearchResult 1 is null", searchResults)
        Assert.assertNotNull("SearchResult 2 is null", searchResults)

        //Positive test for favorite and category
        firstResult?.apply {
            Assert.assertEquals( "Venue name not as expected", "name1", name )
            Assert.assertEquals( "Venue id not as expected", VENUE_ID, id )
            Assert.assertEquals( "Venue favorite not as expected", true, favorite )
            Assert.assertEquals( "Venue distance not as expected", 5, distance )
            Assert.assertTrue( "Venue lat not as expected", latitude == 50.0)
            Assert.assertTrue( "Venue long not as expected", longitude == 25.0 )
            Assert.assertEquals( "Venue categoryt icon url not as expected", "prefix512suffix", categoryIconUrl )
            Assert.assertEquals( "Venue category not as expected", "category", category )
        }

        //Negative test for favorite and category
        secondResult?.apply {
            Assert.assertEquals( "Venue favorite not as expected", false, favorite )
            Assert.assertEquals( "Venue name not as expected", "name2", name )
            Assert.assertEquals( "Venue id not as expected", "notFavoriteVenueId", id )
            Assert.assertNull( "Venue category not null", category )
            Assert.assertNull( "Venue url not null", categoryIconUrl )
        }
    }

    @Test
    @Ignore("Fix scheduling issue")
    fun venueFavorited() {
        val venue = DisplayVenue(id = VENUE_ID, favorite = false)

        venueListViewModel.venueFavorited(venue, 0)
        advanceScheduler()

        verify( venueDBManager, times(1) ).insert( VENUE_ID )
    }

    @Test
    @Ignore("Fix scheduling issue")
    fun venueUnfavorited() {
        val venue = DisplayVenue(id = VENUE_ID, favorite = true)

        venueListViewModel.venueFavorited(venue, 0)
        advanceScheduler()

        verify( venueDBManager, times(1) ).remove( VENUE_ID )

        venueListViewModel.onCleared()
    }

    @Test
    fun googleMapsUrlTest() {
        val key = "key"
        val width = 100
        val height = 200

        val output = venueListViewModel.getGoogleMapsUrl(key, width, height, stubVenue())

        Assert.assertEquals("Google Maps URL not equal", "https://maps.googleapis.com/maps/api/staticmap?" +
                "zoom=15&scale=2&markers=color%3Ared%7C${LAT}%2C${LONG}&markers=color%3Ared%7Clabel%3ASeattle%7C47.60621%2C-122.33207&" +
                "size=${width}x${height}&key=${key}", output)
    }

    /**
     * This method will build a list of venues to be returned in the venue search call
     */
    private fun buildVenueList(): List<Venue> {

        val venueLocationMock = VenueLocation("", "", 50.0, 25.0,  5,
                "", "", "", "", Collections.emptyList())

        val mockVenueIcon = VenueIcon("prefix", "suffix")

        val mockCategory = VenueCategory("", "category", "", "",
                false, mockVenueIcon )

        val venue1 = Venue( VENUE_ID, "name1", "", false, null,
                null, venueLocationMock, listOf(mockCategory), null)


        val venue2: Venue = Venue( "notFavoriteVenueId", "name2", "", false, null,
                null, venueLocationMock, Collections.emptyList(), null)

        return listOf( venue1, venue2 )
    }

    private fun stubVenue() = DisplayVenue(latitude = LAT, longitude = LONG)
}