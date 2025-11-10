package com.exoforce.core.utils

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantConverters {
    @TypeConverter
    fun fromEpoch(value: Long?): Instant? = value?.let { Instant.Companion.fromEpochMilliseconds(it) }

    @TypeConverter
    fun toEpoch(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}