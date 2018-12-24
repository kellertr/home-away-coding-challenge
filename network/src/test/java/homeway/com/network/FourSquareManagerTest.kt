package homeway.com.network

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * This class will perform simple testing against the FourSquareManager class. We will create a mock
 * implementation of the foursquare api class and return mock responses from the foursquareapi. Since
 * we are testing the FourSquareManager, we should only be concerned with the things that the
 * FourSquareManager actually controls.
 */
class FourSquareManagerTest {

    lateinit var fourSquareManager: FourSquareManager
    val api: FourSquareAPI = mock()
    val gson = Gson()

    @Before
    fun setup(){
        fourSquareManager = FourSquareManager(api)
    }

    @Test
    fun placesTest() {

        val searchResult = gson.fromJson(TestUtil.getContentStringFromFile("/place-list.json"),
                VenueSearchResult::class.java)

        //Mock a response from the foursquare api
        whenever( api.getPlaces(any(), any(), any(), any(), any(), any(), any()) ).thenReturn( Single.just(searchResult) )

        fourSquareManager.getPlaces("").test().assertNoErrors().assertValue { venueList ->

            //Validate that the correct mappings of the four square manager class
            Assert.assertTrue("Incorrect venue size", venueList.size == 26  )

            venueList[0].apply {
                Assert.assertEquals("Incorrect venue id", "49d3e558f964a520225c1fe3", this.id  )
                Assert.assertEquals("Incorrect name", "Cherry Street Coffee House", this.name  )
                Assert.assertEquals("Incorrect referral id", "v-1545511280", this.referralId  )
            }

            true
        }

        //Validate that the foursquare manager has correct integration with the FourSquareAPI class
        verify( api, times(1)).getPlaces(any(), any(), any(), any(), any(), any(), any())
    }

    @Test
    fun placeDetailsTest() {
        val venueDetailResult = gson.fromJson(TestUtil.getContentStringFromFile("/place-detail.json"),
                VenueDetailResult::class.java)

        whenever( api.getVenueDetails(any(), any(), any(), any()) ).thenReturn( Single.just(venueDetailResult) )

        fourSquareManager.getVenueDetails("").test().assertNoErrors().assertValue { venue ->

            Assert.assertEquals( "Venue name not equals", "Blue Water Taco Grill", venue.name )
            Assert.assertEquals( "Venue id not equals", "4b2d95d0f964a52040d924e3", venue.id )
            Assert.assertEquals( "Venue url not equals", "http://www.bluewatertacogrill.com", venue.url )

            true
        }

        verify( api, times(1)).getVenueDetails(any(), any(), any(), any())
    }
}