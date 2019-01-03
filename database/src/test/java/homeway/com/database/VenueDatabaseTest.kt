package homeway.com.database

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Test class for the Venue Database implementation, We will test insertion, deletion and retreival of
 * valid 'favorite' entries in the database
 */
@RunWith(AndroidJUnit4::class)
class VenueDatabaseTest {

    private lateinit var venueDao: VenueDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val venueDB = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Application>().applicationContext,
                VenueDatabase::class.java).allowMainThreadQueries().build()
        venueDao = venueDB.venueDao()

    }

    @Test
    fun testVenueInsertion() {
        val id = "1234"
        val dbEntry = VenueEntry(id)

        venueDao.insert(dbEntry)
        venueDao.getFavoriteVenue(id).test().assertNoErrors().assertValue { venue ->

            assertNotNull( "Venue entry is null", venue )
            assertEquals("Venue Id is not equal", venue.venueId, id)

            true
        }
    }

    @Test
    fun testVenueFavoritesList() {
        stubDatabase()

        val dbEntries = listOf("2", "3", "5", "7")

        venueDao.favoriteVenues(venueIds = dbEntries).test().assertNoErrors().assertValue { venueList ->

            assertNotNull( "Venue entry is null", venueList )
            assertEquals("Venue List is oversized", venueList.size, 2)
            assertFalse("Venue Id contains invalid entries", venueList.contains( VenueEntry("1") ))

            true
        }
    }

    @Test
    fun testVenueRemove() {
        stubDatabase()

        venueDao.remove( VenueEntry("1") )

        venueDao.favoriteVenues( venueIds = listOf("1", "2") ).test().assertNoErrors().assertValue {

            assertEquals( "Invalid database size after removal", it.size, 1 )
            true
        }
    }

    private fun stubDatabase(){
        val dbEntry = VenueEntry("1")
        val dbEntry2 = VenueEntry("2")
        val dbEntry3 = VenueEntry("3")
        val dbEntry4 = VenueEntry("4")

        venueDao.insert(dbEntry)
        venueDao.insert(dbEntry2)
        venueDao.insert(dbEntry3)
        venueDao.insert(dbEntry4)
    }
}