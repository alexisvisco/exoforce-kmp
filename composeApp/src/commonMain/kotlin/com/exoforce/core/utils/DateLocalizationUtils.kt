package com.exoforce.core.utils

import androidx.compose.runtime.Composable
import exoforce.composeapp.generated.resources.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object DateLocalizationUtils {

    /**
     * Get the full localized day name (e.g., "Lundi")
     */
    @Composable
    fun getDayOfWeekName(dayOfWeek: DayOfWeek): String {
        return stringResource(getDayOfWeekResource(dayOfWeek))
    }

    /**
     * Get the short localized day name (e.g., "Lun")
     */
    @Composable
    fun getDayOfWeekShort(dayOfWeek: DayOfWeek): String {
        return stringResource(getDayOfWeekShortResource(dayOfWeek))
    }

    /**
     * Get the full localized month name (e.g., "janvier")
     */
    @Composable
    fun getMonthName(month: Month): String {
        return stringResource(getMonthResource(month))
    }

    /**
     * Get the short localized month name (e.g., "janv")
     */
    @Composable
    fun getMonthShort(month: Month): String {
        return stringResource(getMonthShortResource(month))
    }

    /**
     * Format a date with pattern: "Lundi 11 novembre"
     * Pattern: [DayOfWeek] [day] [month]
     */
    @Composable
    fun formatFullDate(date: LocalDate): String {
        val dayOfWeek = getDayOfWeekName(date.dayOfWeek)
        val day = date.dayOfMonth
        val month = getMonthName(date.month)
        return "$dayOfWeek $day $month"
    }

    /**
     * Format a date with pattern: "Lun 11 janv"
     * Pattern: [DayOfWeek short] [day] [month short]
     */
    @Composable
    fun formatShortDate(date: LocalDate): String {
        val dayOfWeek = getDayOfWeekShort(date.dayOfWeek)
        val day = date.dayOfMonth
        val month = getMonthShort(date.month)
        return "$dayOfWeek $day $month"
    }

    /**
     * Format a date with pattern: "11 novembre"
     * Pattern: [day] [month]
     */
    @Composable
    fun formatDateWithoutDayOfWeek(date: LocalDate): String {
        val day = date.dayOfMonth
        val month = getMonthName(date.month)
        return "$day $month"
    }

    /**
     * Get the string resource for a day of week
     */
    fun getDayOfWeekResource(dayOfWeek: DayOfWeek): StringResource {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> Res.string.day_monday
            DayOfWeek.TUESDAY -> Res.string.day_tuesday
            DayOfWeek.WEDNESDAY -> Res.string.day_wednesday
            DayOfWeek.THURSDAY -> Res.string.day_thursday
            DayOfWeek.FRIDAY -> Res.string.day_friday
            DayOfWeek.SATURDAY -> Res.string.day_saturday
            DayOfWeek.SUNDAY -> Res.string.day_sunday
        }
    }

    /**
     * Get the string resource for a short day of week
     */
    fun getDayOfWeekShortResource(dayOfWeek: DayOfWeek): StringResource {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> Res.string.day_monday_short
            DayOfWeek.TUESDAY -> Res.string.day_tuesday_short
            DayOfWeek.WEDNESDAY -> Res.string.day_wednesday_short
            DayOfWeek.THURSDAY -> Res.string.day_thursday_short
            DayOfWeek.FRIDAY -> Res.string.day_friday_short
            DayOfWeek.SATURDAY -> Res.string.day_saturday_short
            DayOfWeek.SUNDAY -> Res.string.day_sunday_short
        }
    }

    /**
     * Get the string resource for a month
     */
    fun getMonthResource(month: Month): StringResource {
        return when (month) {
            Month.JANUARY -> Res.string.month_january
            Month.FEBRUARY -> Res.string.month_february
            Month.MARCH -> Res.string.month_march
            Month.APRIL -> Res.string.month_april
            Month.MAY -> Res.string.month_may
            Month.JUNE -> Res.string.month_june
            Month.JULY -> Res.string.month_july
            Month.AUGUST -> Res.string.month_august
            Month.SEPTEMBER -> Res.string.month_september
            Month.OCTOBER -> Res.string.month_october
            Month.NOVEMBER -> Res.string.month_november
            Month.DECEMBER -> Res.string.month_december
            else -> Res.string.month_january // fallback
        }
    }

    /**
     * Get the string resource for a short month
     */
    fun getMonthShortResource(month: Month): StringResource {
        return when (month) {
            Month.JANUARY -> Res.string.month_january_short
            Month.FEBRUARY -> Res.string.month_february_short
            Month.MARCH -> Res.string.month_march_short
            Month.APRIL -> Res.string.month_april_short
            Month.MAY -> Res.string.month_may_short
            Month.JUNE -> Res.string.month_june_short
            Month.JULY -> Res.string.month_july_short
            Month.AUGUST -> Res.string.month_august_short
            Month.SEPTEMBER -> Res.string.month_september_short
            Month.OCTOBER -> Res.string.month_october_short
            Month.NOVEMBER -> Res.string.month_november_short
            Month.DECEMBER -> Res.string.month_december_short
            else -> Res.string.month_january_short // fallback
        }
    }
}
