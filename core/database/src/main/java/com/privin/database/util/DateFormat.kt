package com.privin.database.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

fun getToday(): String = Clock.System.todayIn(TimeZone.UTC).run {
    "${this.dayOfMonth}-${this.monthNumber}-${this.year}"
}