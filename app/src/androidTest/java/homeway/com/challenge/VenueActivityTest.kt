package homeway.com.challenge

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule

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
    }

    companion object {
        private const val SEARCH_TERM = "h"
    }
}
