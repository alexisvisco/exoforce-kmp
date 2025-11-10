package com.exoforce.data.domain

import kotlinx.datetime.Instant

data class Program (
    val id: String,
    val name: String,
    val userId: String,
    val createdAt: Instant,
    val updatedAt: Instant,

    val workouts: List<Workout> = emptyList(),
    val user: User? = null
)