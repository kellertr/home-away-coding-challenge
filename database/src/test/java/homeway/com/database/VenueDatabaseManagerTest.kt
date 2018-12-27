package homeway.com.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * This test class will test insertion, venue favorites and deletion from the database. We will only
 * be concerned with the implementation of the VenueDatabaseManager and not the implementation of VenueDatabase
 * or VenueDao in this test.
 */
class VenueDatabaseManagerTest {

    lateinit var venueDatabaseManager: VenueDatabaseManager
    private val venueDao: VenueDao = mock()
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        venueDatabaseManager = VenueDatabaseManager(venueDao)
    }

    @Test
    fun testVenueInsertion() {
        venueDatabaseManager.insert("1234")
        verify(venueDao, times(1)).insert( any() )
    }

    @Test
    fun testVenueFavoritesList() {
        whenever(venueDao.favoriteVenues(any())).thenReturn( Single.just(listOf(
                VenueEntry("1"),
                VenueEntry("2"),
                VenueEntry("3"),
                VenueEntry("4")
        ) ))

        venueDatabaseManager.favoriteVenues( listOf("1", "2", "3", "4", "5", "6") ).test().assertNoErrors().assertValue { venues ->

            Assert.assertEquals("Incorrect venue list size", venues.size, 4)
            Assert.assertFalse( "Contains invalid favorite", venues.contains("6"))

            true
        }

        verify(venueDao, times(1)).favoriteVenues(any())
    }

    @Test
    fun testVenueRemove() {
        venueDatabaseManager.remove("1234")
        verify(venueDao, times(1)).remove(any())
    }

}