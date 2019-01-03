package homeway.com.challenge.test.mock

/**
 * A MockItem is a class that represents a URL that will be mocked. Currently, each URL is matched with
 * a '.contains()' on the path matcher which will then trigger the opening of the file in 'fileName'
 * The responseCode field is what we would like the service to respond with
 */
class MockItem( val path :String  = "",
                val filePath: String = "",
                val responseCode: Int = 200)
