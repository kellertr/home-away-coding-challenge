package homeway.com.challenge.test

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import homeway.com.challenge.R
import homeway.com.challenge.VenueActivity
import homeway.com.challenge.test.mock.MockDispatcher
import homeway.com.challenge.test.mock.MockItem
import okhttp3.mockwebserver.Dispatcher

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class VenueActivityTest : BaseEspressoTest() {

    @get:Rule
    var activityRule: ActivityTestRule<VenueActivity>
            = ActivityTestRule(VenueActivity::class.java)

    @Test
    fun validateSearch() {
        onView(withId(R.id.search_src_text))
                .perform(typeText(SEARCH_TERM))

        closeSoftKeyboard()

        Thread.sleep( 1000 )

    }

    override fun getDispatcher(): Dispatcher {
        val mockDispatcher = MockDispatcher()

        mockDispatcher.mockedItems = listOf(
                MockItem( "v2/venues/search", "/places-list.json" ),
                MockItem( "v2/venues/4b2d95d0f964a52040d924e3", "/places-detail.json" )
        )

        return mockDispatcher
    }

    companion object {
        private const val SEARCH_TERM = "c"
    }
}
