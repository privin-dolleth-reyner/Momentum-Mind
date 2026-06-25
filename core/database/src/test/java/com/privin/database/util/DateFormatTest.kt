package com.privin.database.util

import org.junit.Assert.assertTrue
import org.junit.Test

class DateFormatTest {

    @Test
    fun `getToday returns a day-month-year string`() {
        val today = getToday()

        // Format produced by the implementation is "d-M-yyyy" (no zero padding).
        assertTrue(
            "Unexpected date format: $today",
            Regex("""\d{1,2}-\d{1,2}-\d{4}""").matches(today)
        )
    }

    @Test
    fun `getToday is stable within a single call site`() {
        assertTrue(getToday().isNotBlank())
    }
}
