package homeway.com.network

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

import org.junit.Before
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert


/**
 * This class will run unit tests on our FourSquareAPI implementation. To mimic network traffic, we
 * we will utilize a mock webserver to enqueue service responses and validate requests were made with
 * appropriate query parameters, etc.
 */
class FourSquareApiTest {

    lateinit var api: FourSquareAPI
    private val mockWebServer = MockWebServer()

    @Before
    fun setup(){
        mockWebServer.start()

        //Initialize a custom instance of the foursquare api so we can utilize a mock web server and
        // avoid hitting square's api's real time
        val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(FourSquareAPI::class.java)
    }

    @After
    fun teardown(){
        mockWebServer.shutdown()
    }

    @Test
    fun placesTest() {
        //Enqueue a place list response onto our mock web server
        mockWebServer.enqueue(MockResponse().setBody(TestUtil.getContentStringFromFile("/place-list.json")))

        //Call the foursquare api and validate that the service mapping is correct by validating some
        //service fields
        api.getPlaces( searchTerm = "Coffee" ).test().assertNoErrors().assertValue { result ->
            Assert.assertNotNull("Places result is null", result)
            Assert.assertTrue("Incorrect venue size", result.response?.venues?.size == 26  )

            //Verify components of the service result
            result.response?.venues?.get(0)?.apply {
                Assert.assertEquals("Incorrect venue id", "49d3e558f964a520225c1fe3", this.id  )
                Assert.assertEquals("Incorrect name", "Cherry Street Coffee House", this.name  )
                Assert.assertEquals("Incorrect referral id", "v-1545511280", this.referralId  )
            }

            true
        }

        //Verify the url and HTTP Protocol of the service call
        val request = mockWebServer.takeRequest()
        Assert.assertEquals("Wrong HTTP Request Type", "GET", request.method)
        Assert.assertEquals( "path incorrect",
                "/v2/venues/search?ll=47.60621%2C-122.33207&v=20181222&" +
                        "client_secret=QZWIXG4TA5M1CWZXBQECHEFAJKM0SUJVRYKL5JB5IIO5RV4C&" +
                        "client_id=DZQHWPGCCQQNDID5ZDYQUSKJN1EMDWCHFENPKK3RFNVWU511&" +
                        "radius=500&intent=browse&query=Coffee",
                request.path)
    }

    @Test
    fun venueDetailsTest() {
        mockWebServer.enqueue(MockResponse().setBody(TestUtil.getContentStringFromFile("/place-detail.json")))
        api.getVenueDetails("893490834093").test().assertNoErrors().assertValue { result ->

            Assert.assertNotNull("Venue Details Result is null", result)

            result.response?.venue?.apply {
                Assert.assertEquals( "Venue name not equals", "Blue Water Taco Grill", this.name )
                Assert.assertEquals( "Venue id not equals", "4b2d95d0f964a52040d924e3", this.id )
                Assert.assertEquals( "Venue url not equals", "http://www.bluewatertacogrill.com", this.url )
            }

            true
        }

        val request = mockWebServer.takeRequest()
        Assert.assertEquals( "Request method not equal", "GET", request.method )
        Assert.assertEquals("Request path not equal", "/v2/venues/893490834093?v=20181222&" +
                "client_secret=QZWIXG4TA5M1CWZXBQECHEFAJKM0SUJVRYKL5JB5IIO5RV4C&" +
                "client_id=DZQHWPGCCQQNDID5ZDYQUSKJN1EMDWCHFENPKK3RFNVWU511", request.path)
    }
}