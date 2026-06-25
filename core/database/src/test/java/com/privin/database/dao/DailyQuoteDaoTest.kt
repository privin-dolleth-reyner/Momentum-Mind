package com.privin.database.dao

import androidx.room.Room
import com.privin.database.MomentumMindDatabase
import com.privin.database.model.DailyQuoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DailyQuoteDaoTest {

    private lateinit var database: MomentumMindDatabase
    private lateinit var dao: DailyQuoteDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            MomentumMindDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.dailyQuoteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and read back a quote by date`() = runTest {
        val entity = DailyQuoteEntity(date = "25-6-2026", quote = "Today", author = "Me")

        dao.insertDailyQuote(entity)

        val loaded = dao.getDailyQuoteByDate("25-6-2026").first()
        assertEquals("Today", loaded.quote)
        assertEquals("Me", loaded.author)
    }

    @Test
    fun `hasQuotes and isDailyQuoteAvailable reflect inserted rows`() = runTest {
        assertFalse(dao.hasQuotes())
        assertFalse(dao.isDailyQuoteAvailable("1-1-2026"))

        dao.insertDailyQuote(DailyQuoteEntity(date = "1-1-2026", quote = "q", author = "a"))

        assertTrue(dao.hasQuotes())
        assertTrue(dao.isDailyQuoteAvailable("1-1-2026"))
    }

    @Test
    fun `getLastAvailable returns the most recent row by date ordering`() = runTest {
        dao.insertDailyQuote(DailyQuoteEntity(date = "1-1-2026", quote = "first", author = "a"))
        dao.insertDailyQuote(DailyQuoteEntity(date = "2-1-2026", quote = "second", author = "b"))

        // Note: ordering is lexicographic on the date string.
        val last = dao.getLastAvailable().first()
        assertEquals("second", last.quote)
    }

    @Test
    fun `favourite then unfavourite a past-dated quote updates the same row`() = runTest {
        val pastDate = "10-6-2026"
        dao.insertDailyQuote(
            DailyQuoteEntity(date = pastDate, quote = "Old", author = "Past", isFavourite = false)
        )

        // Not favourited yet.
        assertTrue(dao.getFavourites().first().isEmpty())

        // Favourite it.
        val stored = dao.getDailyQuoteByDate(pastDate).first()
        dao.updateDailyQuote(stored.copy(isFavourite = true))

        val favourites = dao.getFavourites().first()
        assertEquals(1, favourites.size)
        assertEquals(pastDate, favourites.first().date)

        // Unfavourite it — must target the same (past-dated) row.
        dao.updateDailyQuote(stored.copy(isFavourite = false))
        assertTrue(dao.getFavourites().first().isEmpty())

        // The row still exists, just no longer favourited.
        assertFalse(dao.getDailyQuoteByDate(pastDate).first().isFavourite)
    }
}
