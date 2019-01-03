package homeway.com.challenge.test

import android.view.View
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
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
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import homeway.com.challenge.view.VenueViewHolder
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf


/**
 * The VenueActivityTest will test all components of the application and will mock them using
 * controlled responses from MockWebServer
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class VenueActivityTest : BaseEspressoTest() {

    @get:Rule
    var activityRule: ActivityTestRule<VenueActivity>
            = ActivityTestRule(VenueActivity::class.java)

    //Perform a simple UI test over the search fragment for additional validation
    @Test
    fun validateSearch() {
        onView(withId(R.id.search_src_text))
                .perform(typeText(SEARCH_TERM))

        closeSoftKeyboard()

        waitForId(R.id.venueImage)

        //Verify that service results are displayed as expected
        onView(withId(R.id.venueList))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.venueImage),
                        isDisplayed())))))

        onView(withId(R.id.venueList))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.venueName),
                        withText("Cherry Street Coffee House"))))))

        onView(withId(R.id.venueList))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.venueCategory),
                        withText("Coffee Shop"))))))

        onView(withId(R.id.venueList))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.venueDistance),
                        withText("413 meters from Center Seattle"))))))

        onView(withId(R.id.venueList))
                .check(matches(withViewAtPosition(0, hasDescendant(allOf(withId(R.id.venueFavorite),
                        isDisplayed())))))
    }

    //Perform a simple UI test of the VenueDetails page
    @Test
    fun validateDetailPage() {
        onView(withId(R.id.search_src_text))
                .perform(typeText(SEARCH_TERM))

        closeSoftKeyboard()

        waitForId(R.id.venueImage)

        onView(withId(R.id.venueList))
                .perform(RecyclerViewActions.actionOnItemAtPosition<VenueViewHolder>(0, click()))

        waitForId(R.id.venueDetailLink)

        onView(withId(R.id.venueDetailLink)).check( matches( isDisplayed() ) )
    }

    override fun getDispatcher(): Dispatcher {
        val mockDispatcher = MockDispatcher()

        //Return the rules for the MockWebServer that we will be utilizing for this test class
        mockDispatcher.mockedItems = listOf(
                MockItem( "v2/venues/search", "/places-list.json" ),
                MockItem( "/v2/venues/49d3e558f964a520225c1fe3", "/places-detail.json" )
        )

        return mockDispatcher
    }

    companion object {
        private const val SEARCH_TERM = "c"
    }
}

/**
 * Method used in conjunction with testing a recycler view.
 *
 * @param position is the position in the recycler view we want to access
 * @param itemMatcher is the matcher we are sending to the recycler view to apply to the itemView
 *        of a viewholder at a given position
 * @return a CustomViewMatcher that will make sure the recycler view item exists and meets all
 *         criteria passed in from the item matcher
 */
fun withViewAtPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
        }
    }
}
