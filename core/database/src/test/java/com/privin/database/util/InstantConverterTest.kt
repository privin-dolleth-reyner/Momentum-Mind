package com.privin.database.util

import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InstantConverterTest {

    private val converter = InstantConverter()

    @Test
    fun `instant round-trips through long`() {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000)

        val asLong = converter.instantToLong(instant)
        val back = converter.longToInstant(asLong)

        assertEquals(1_700_000_000_000, asLong)
        assertEquals(instant, back)
    }

    @Test
    fun `null values convert to null`() {
        assertNull(converter.instantToLong(null))
        assertNull(converter.longToInstant(null))
    }
}
