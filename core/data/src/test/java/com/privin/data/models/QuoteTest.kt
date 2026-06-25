package com.privin.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteTest {

    @Test
    fun `mapToDailyQuoteEntity round-trips every field including date and favourite`() {
        val quote = Quote(
            date = "12-3-2024",
            quote = "Persist",
            author = "Helen",
            isFavorite = true
        )

        val entity = quote.mapToDailyQuoteEntity()

        assertEquals("12-3-2024", entity.date)
        assertEquals("Persist", entity.quote)
        assertEquals("Helen", entity.author)
        assertTrue(entity.isFavourite)
    }

    @Test
    fun `isEmpty is true when quote and author are blank`() {
        assertTrue(Quote(quote = "", author = "").isEmpty())
    }

    @Test
    fun `isEmpty is false when quote is present`() {
        assertFalse(Quote(quote = "Hello", author = "").isEmpty())
    }

    @Test
    fun `isEmpty is false when author is present`() {
        assertFalse(Quote(quote = "", author = "Someone").isEmpty())
    }
}
