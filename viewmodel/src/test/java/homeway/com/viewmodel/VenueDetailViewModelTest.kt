package homeway.com.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import homeway.com.model.venue.Venue
import homeway.com.network.FourSquareManager
import homeway.com.viewmodel.model.DisplayVenue
import io.reactivex.Single
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * This is a simple test of the VenueDetailViewModel. We will mock the implementations of
 * FourSquareManager since we are only concerned with the implementation of VenueDetailViewModel
 * in this test class.
 */
@RunWith(AndroidJUnit4::class)
class VenueDetailViewModelTest : BaseViewModelTest() {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val LAT = 25.0
    private val LONG = 50.0
    private val ID = "ID"
    private val URL = "www.homeaway.com"

    private val fourSquareManager: FourSquareManager = mock()
    lateinit var viewModel: VenueDetailViewModel

    @Before
    override fun setup(){
        super.setup()

        viewModel = spy(VenueDetailViewModel(fourSquareManager))
    }

    @Test
    fun googleMapsUrlTest() {
        viewModel.venueLiveData.value = stubVenue()

        val key = "key"
        val width = 100
        val height = 200

        val output = viewModel.getGoogleMapsUrl(key, width, height)

        assertEquals( "Google Maps URL not equal", "https://maps.googleapis.com/maps/api/staticmap?" +
                "zoom=15&scale=2&markers=color%3Ared%7C${LAT}%2C${LONG}&markers=color%3Ared%7Clabel%3ASeattle%7C47.60621%2C-122.33207&" +
                "size=${width}x${height}&key=${key}", output )
    }

    @Test
    fun getVenueDetailsTest() {
        viewModel.venueLiveData.value = stubVenue()

        val networkVenue: Venue = mock()
        whenever(networkVenue.url).thenReturn(URL)
        whenever( fourSquareManager.getVenueDetails(ID) ).thenReturn(Single.just(networkVenue))

        viewModel.getVenueDetails( stubVenue() )
        advanceScheduler()

        assertEquals( "Url not updated correctly", URL, viewModel.venueLiveData.value?.url )

    }

    private fun stubVenue() = DisplayVenue(latitude = LAT, longitude = LONG, id = "ID")

}