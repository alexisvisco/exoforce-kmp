package com.exoforce.core.utils

import DayMonthYear
import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class DayMonthYearConverters {
    @TypeConverter
    fun fromDayMonthYear(value: DayMonthYear?): String? {
        return value?.date?.toString()
    }

    @TypeConverter
    fun toDayMonthYear(value: String?): DayMonthYear? {
        return if (value.isNullOrEmpty()) {
            null
        } else {
            try {
                DayMonthYear(LocalDate.parse(value))
            } catch (e: Exception) {
                null
            }
        }
    }
}