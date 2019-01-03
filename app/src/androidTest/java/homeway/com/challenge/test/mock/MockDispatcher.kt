package homeway.com.challenge.test.mock

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.IOException
import java.util.*

class MockDispatcher: Dispatcher() {
    private val TAG = MockDispatcher::class.java.simpleName

    var mockedItems: List<MockItem> = Collections.emptyList()

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