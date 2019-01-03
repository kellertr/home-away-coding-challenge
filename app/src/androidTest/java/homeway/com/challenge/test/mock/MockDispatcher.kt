package homeway.com.challenge.test.mock

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.IOException
import java.util.*

/**
 * The MockDispatcher class is used by MockWebServer to map our network requests with responses
 */
class MockDispatcher: Dispatcher() {
    private val TAG = MockDispatcher::class.java.simpleName

    //The list of all of the mocks that this dispatcher supports
    var mockedItems: List<MockItem> = Collections.emptyList()

    /**
     * This method will handle all mappings that we have configured in our MockWebServer implementation.
     * We attempt to create a mapping by doing a url match and opening a file if the url from the server
     * matches one that we have in our list of mocked items. It will default to a 404 if there is
     * no mapping found.
     *
     * @param request is the request passed from MockWebServer
     * @return a mock response for the Network request handled by MockWebServer
     */
    override fun dispatch(request: RecordedRequest?): MockResponse {
        var response = MockResponse().setResponseCode(404)
        for (item in mockedItems) {
            if (request?.path?.contains(item.path) == true) {
                try {
                    Log.d(TAG, "request path :: URL " + request.path)
                    response = response.setResponseCode(item.responseCode)
                            .setBody(MockingResponseProvider.getContentStringFromFile(item.filePath))
                } catch (e: IOException) {
                    Log.e(TAG, "Caught Error reading response" + request.path)
                    return response
                }

            }
        }

        return response
    }
}