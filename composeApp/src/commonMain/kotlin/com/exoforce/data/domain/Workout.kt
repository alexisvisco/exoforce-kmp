package com.exoforce.data.domain

import DayMonthYear
import kotlinx.datetime.Instant


data class Workout (
    val id: String,
    val programId: String,
    val day: DayMonthYear,
    val durationSec: Int?,
    val startedAt: Instant?,
    val endedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,

    val exercises: List<Exercise> = emptyList(),
    val program: Program? = null
)