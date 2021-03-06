package homeway.com.network


import homeway.com.model.venue.Venue
import io.reactivex.Single
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.security.cert.CertificateException


/**
 * The FourSquareAPI is a retrofit interface that will be utilized to do a simple venue search and
 * venue detail lookup. For simplicity and cleanliness, this class will only interact with the REST
 * services provided by FourSqaure and nothing else. For Business Logic implementation
 * @see homeaway.com.network.FourSquareManager
 */
interface FourSquareAPI {

    /**
     * Retrieve a list of places from the four square api for a given search term.
     *
     * @param searchTerm is the user initiated search term we pass to the four square API
     * @return a single that will manage interaction with the four square place list rest API
     */
    @GET("v2/venues/search")
    fun getPlaces(@Query("ll") location: String = SEATTLE_LAT_LONG,
                  @Query("v") version: String = API_VERSION,
                  @Query("client_secret") clientSecret: String = CLIENT_SECRET,
                  @Query("client_id") clientId: String = CLIENT_ID,
                  @Query("radius") radius: Int = RADIUS,
                  @Query("intent") intent: String = SEARCH_INTENT,
                  @Query("query") searchTerm: String
                  ): Single<VenueSearchResult>

    /**
     * Retrieve details for a specific venue from the four square api
     *
     * @param venueId is the venueID for a place we are looking for
     * @return a single that will manage interaction with the four square place detail rest API
     */
    @GET( "v2/venues/{venueId}" )
    fun getVenueDetails(@Path("venueId") venueId: String,
                        @Query("v") version: String = API_VERSION,
                        @Query("client_secret") clientSecret: String = CLIENT_SECRET,
                        @Query("client_id") clientId: String = CLIENT_ID): Single<VenueDetailResult>

    companion object Factory {

        const val BASE_URL = "https://api.foursquare.com"
        const val SEATTLE_LAT_LONG = "47.60621,-122.33207"
        const val API_VERSION = "20181222"
        const val CLIENT_SECRET = "QZWIXG4TA5M1CWZXBQECHEFAJKM0SUJVRYKL5JB5IIO5RV4C"
        const val CLIENT_ID = "DZQHWPGCCQQNDID5ZDYQUSKJN1EMDWCHFENPKK3RFNVWU511"
        const val SEARCH_INTENT = "browse"
        const val RADIUS = 500

        const val MOCK_URL = "http://localhost:8080/"
        const val MOCK_PORT = 8080

        /**
         * Create an instance of the FourSquareAPI utilizing RX and Gson.
         *
         * @return a new instance of the FourSquare API
         */
        fun create(): FourSquareAPI {
            return create(BASE_URL)
        }

        /**
         * Create an instance of the FourSquareAPI utilizing RX and Gson.
         *
         * @param baseUrl is the baseURL for the FourSquareAPI
         * @return a new instance of the FourSqaure API
         */
        fun create( baseUrl: String ): FourSquareAPI{

            //Trust all certificates, typically we would only want a limited subset of certificates
            //to prevent man in the middle attacks
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    //No-op
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    //No-op
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val okHttpBuilder = OkHttpClient.Builder()
            okHttpBuilder.sslSocketFactory(sslContext.socketFactory)
            okHttpBuilder.hostnameVerifier { _ , _ ->
                true
            }

            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpBuilder.build())
                    .baseUrl(baseUrl)
                    .build()

            return retrofit.create(FourSquareAPI::class.java)
        }
    }
}

/*
 * The following four classes are wrapper classes for service responses that are returned from
 * FourSquare
 */

data class VenueSearchResult(val response: InnerVenueSearchResultResponse?)
data class InnerVenueSearchResultResponse(val venues: List<Venue>?)

data class VenueDetailResult(val response: InnerVenueDetailResult?)
data class InnerVenueDetailResult(val venue: Venue?)