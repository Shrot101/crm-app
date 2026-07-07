package com.akashicsoft.crm.activity.util

import kotlinx.datetime.DateTimeUnit
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

object CalendarUtils {
    @OptIn(ExperimentalTime::class)
    fun today(): LocalDate {
        return Clock.System.todayIn(TimeZone.currentSystemDefault())
    }

    fun getWeek(date: LocalDate): List<LocalDate> {
        val daysFromMonday = date.dayOfWeek.isoDayNumber - 1
        val monday = date.minus(daysFromMonday, DateTimeUnit.DAY)

        return List(7) { index ->
            monday.plus(index, DateTimeUnit.DAY)
        }
    }

    fun nextWeek(date: LocalDate): LocalDate {
        return date.plus(7, DateTimeUnit.DAY)
    }

    fun previousWeek(date: LocalDate): LocalDate {
        return date.minus(7, DateTimeUnit.DAY)
    }

    fun monthTitle(date: LocalDate): String {
        val monthName = date.month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }

        return "$monthName ${date.year}"
    }

    fun isToday(date: LocalDate): Boolean {
        return date == today()
    }

    fun dayLabel(date: LocalDate): String {
        return date.dayOfWeek.name.take(3).uppercase()
    }

    fun fullDateHeader(date: LocalDate): String {
        val dayName = date.dayOfWeek.name.uppercase()
        val monthName = date.month.name.uppercase()
        val day = date.day
        return "$dayName, $monthName $day"
    }

    fun parseTimeToMinutes(time: String): Int {
        if (time.equals("All Day", ignoreCase = true)) return -1 // Earliest possible

        val parts = time.split(" ")
        if (parts.size < 2) return 0 // Fallback

        val timeParts = parts[0].split(":")
        if (timeParts.size < 2) return 0 // Fallback

        var hours = timeParts[0].toIntOrNull() ?: 0
        val minutes = timeParts[1].toIntOrNull() ?: 0
        val amPm = parts[1].uppercase()

        if (amPm == "PM" && hours < 12) hours += 12
        if (amPm == "AM" && hours == 12) hours = 0

        return hours * 60 + minutes
    }
}
