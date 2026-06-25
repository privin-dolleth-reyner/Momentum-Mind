package com.privin.data

import app.cash.turbine.test
import com.privin.data.models.Quote
import com.privin.database.dao.DailyQuoteDao
import com.privin.database.dao.QuoteDao
import com.privin.database.model.DailyQuoteEntity
import com.privin.network.Server
import com.privin.network.model.QuoteData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuotesRepositoryImplTest {

    private val server: Server = mockk(relaxed = true)
    private val dailyQuoteDao: DailyQuoteDao = mockk(relaxed = true)
    private val quoteDao: QuoteDao = mockk(relaxed = true)

    private lateinit var repository: QuotesRepositoryImpl

    @Before
    fun setUp() {
        repository = QuotesRepositoryImpl(server, dailyQuoteDao, quoteDao)
    }

    @Test
    fun `getDailyQuote emits cached quote and does not hit the server when one exists`() = runTest {
        val entity = DailyQuoteEntity(
            date = "25-6-2026",
            quote = "Cached wisdom",
            author = "Cache",
            isFavourite = true
        )
        coEvery { dailyQuoteDao.getDailyQuoteByDate(any()) } returns flowOf(entity)

        repository.getDailyQuote().test {
            val quote = awaitItem()
            assertEquals("Cached wisdom", quote.quote)
            assertEquals("25-6-2026", quote.date)
            assertTrue(quote.isFavorite)
            awaitComplete()
        }

        coVerify(exactly = 0) { server.getDailyQuote() }
    }

    @Test
    fun `getDailyQuote syncs from server and inserts when nothing is cached`() = runTest {
        coEvery { dailyQuoteDao.getDailyQuoteByDate(any()) } returns emptyFlow()
        coEvery { server.getDailyQuote() } returns QuoteData(quote = "Fresh", author = "Net")

        // The sync runs eagerly inside getDailyQuote() before the flow is returned.
        repository.getDailyQuote()

        coVerify(exactly = 1) { server.getDailyQuote() }
        coVerify(exactly = 1) { dailyQuoteDao.insertDailyQuote(any()) }
    }

    @Test
    fun `updateFavorites persists an entity whose date matches the quote`() = runTest {
        val entitySlot = slot<DailyQuoteEntity>()
        coEvery { dailyQuoteDao.updateDailyQuote(capture(entitySlot)) } returns Unit

        val quote = Quote(
            date = "10-6-2026",
            quote = "Old favourite",
            author = "Past",
            isFavorite = false
        )
        repository.updateFavorites(quote)

        // The update must target the original row (by date), otherwise unfavourite no-ops.
        assertEquals("10-6-2026", entitySlot.captured.date)
        assertEquals("Old favourite", entitySlot.captured.quote)
        assertEquals(false, entitySlot.captured.isFavourite)
    }

    @Test
    fun `getFavourites maps entities to quotes preserving date and favourite flag`() = runTest {
        val entities = listOf(
            DailyQuoteEntity(date = "1-1-2026", quote = "A", author = "x", isFavourite = true),
            DailyQuoteEntity(date = "2-1-2026", quote = "B", author = "y", isFavourite = true)
        )
        coEvery { dailyQuoteDao.getFavourites() } returns flowOf(entities)

        repository.getFavourites().test {
            val quotes = awaitItem()
            assertEquals(2, quotes.size)
            assertEquals("1-1-2026", quotes[0].date)
            assertEquals("B", quotes[1].quote)
            assertTrue(quotes.all { it.isFavorite })
            awaitComplete()
        }
    }
}
