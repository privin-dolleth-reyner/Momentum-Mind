package com.privin.data.mappers

import com.privin.database.model.QuoteEntity
import com.privin.network.model.QuoteData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class QuoteMapperTest {

    @Test
    fun `QuoteData mapToQuoteEntity copies quote author charCount and html`() {
        val data = QuoteData(
            quote = "Dream big",
            author = "Ada",
            charCount = "9",
            htmlString = "<p>Dream big</p>"
        )

        val entity = data.mapToQuoteEntity()

        assertEquals("Dream big", entity.quote)
        assertEquals("Ada", entity.author)
        assertEquals("9", entity.charCount)
        assertEquals("<p>Dream big</p>", entity.htmlString)
    }

    @Test
    fun `QuoteData mapToQuote copies quote and author`() {
        val data = QuoteData(quote = "Do it", author = "Nike")

        val quote = data.mapToQuote()

        assertEquals("Do it", quote.quote)
        assertEquals("Nike", quote.author)
        assertFalse(quote.isFavorite)
    }

    @Test
    fun `QuoteEntity mapToQuote copies quote and author`() {
        val entity = QuoteEntity(
            id = 5,
            quote = "Onwards",
            author = "Grace",
            charCount = "7",
            htmlString = "<p>Onwards</p>"
        )

        val quote = entity.mapToQuote()

        assertEquals("Onwards", quote.quote)
        assertEquals("Grace", quote.author)
        assertFalse(quote.isFavorite)
    }
}
