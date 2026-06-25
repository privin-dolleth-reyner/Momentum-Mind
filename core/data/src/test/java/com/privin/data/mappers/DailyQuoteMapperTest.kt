package com.privin.data.mappers

import com.privin.database.model.DailyQuoteEntity
import com.privin.network.model.QuoteData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyQuoteMapperTest {

    @Test
    fun `mapToQuote preserves the entity date so favourite updates target the right row`() {
        val entity = DailyQuoteEntity(
            date = "10-6-2026",
            quote = "Stay hungry",
            author = "Steve",
            htmlString = "<p>Stay hungry</p>",
            isFavourite = true
        )

        val quote = entity.mapToQuote()

        // Regression guard: date is the primary key used by @Update. If it is dropped here,
        // favourite/unfavourite silently no-ops for any quote not dated today.
        assertEquals("10-6-2026", quote.date)
        assertEquals("Stay hungry", quote.quote)
        assertEquals("Steve", quote.author)
        assertTrue(quote.isFavorite)
    }

    @Test
    fun `mapToQuote maps a non-favourite entity correctly`() {
        val entity = DailyQuoteEntity(
            date = "1-1-2025",
            quote = "Keep going",
            author = "Anon",
            isFavourite = false
        )

        val quote = entity.mapToQuote()

        assertEquals("1-1-2025", quote.date)
        assertFalse(quote.isFavorite)
    }

    @Test
    fun `mapToQuote on null entity returns an empty default Quote`() {
        val nullEntity: DailyQuoteEntity? = null

        val quote = nullEntity.mapToQuote()

        assertTrue(quote.isEmpty())
        assertEquals("", quote.quote)
        assertEquals("", quote.author)
        assertFalse(quote.isFavorite)
    }

    @Test
    fun `QuoteData mapToDailyQuoteEntity maps quote author and html`() {
        val data = QuoteData(
            quote = "Be brave",
            author = "Maya",
            charCount = "8",
            htmlString = "<p>Be brave</p>"
        )

        val entity = data.mapToDailyQuoteEntity()

        assertEquals("Be brave", entity.quote)
        assertEquals("Maya", entity.author)
        assertEquals("<p>Be brave</p>", entity.htmlString)
        assertFalse(entity.isFavourite)
    }
}
