package com.exoforce.data.remote

import com.exoforce.core.serialization.InstantSerializer
import com.exoforce.data.remote.types.RemoteExercise
import com.exoforce.data.remote.types.RemotePerformedExercise
import com.exoforce.data.remote.types.RemotePerformedExerciseSet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

class PerformedExerciseClient(private val httpClient: HttpClient) {
    @Serializable
    data class CreatePerformedExerciseRequest(
        @SerialName("exercise_id")
        val exerciseId: String,
        @SerialName("started_at")
        @Serializable(with = InstantSerializer::class)
        val startedAt: Instant,
        @SerialName("completed_at")
        @Serializable(with = InstantSerializer::class)
        val completedAt: Instant,
        @SerialName("total_duration_sec")
        val totalDurationSec: Int,
        val rpe: Int,
        val notes: String,
        val sets: List<CreatePerformedExerciseSetRequest>,
        @SerialName("workout_id")
        val workoutId: String
    )

    @Serializable
    data class CreatePerformedExerciseSetRequest(
        @SerialName("exercise_set_id")
        val exerciseSetId: String?,
        val position: Int,
        @SerialName("started_at")
        @Serializable(with = InstantSerializer::class)
        val startedAt: Instant,
        @SerialName("completed_at")
        @Serializable(with = InstantSerializer::class)
        val completedAt: Instant?,
        val repetitions: Int?,
        @SerialName("effort_duration_sec")
        val effortDurationSec: Int?,
        @SerialName("weight_kg")
        val weightKg: Double?,
        @SerialName("distance_in_meters")
        val distanceInMeters: Double?,
        @SerialName("hold_size_millimeters")
        val holdSizeMillimeters: Int?
    )

    @Serializable
    data class CreatePerformedExerciseResponse(
        val id: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("workout_id")
        val workoutId: String,

        @SerialName("exercise_id")
        val exerciseId: String,
        @SerialName("started_at")
        @Serializable(with = InstantSerializer::class)
        val startedAt: Instant,
        @SerialName("completed_at")
        @Serializable(with = InstantSerializer::class)
        val completedAt: Instant?,
        @SerialName("total_duration_sec")
        val totalDurationSec: Int?,
        val rpe: Int?,
        val notes: String,
        val sets: List<RemotePerformedExerciseSet> = emptyList(),
        val exercise: RemoteExercise? = null
    )

    suspend fun createPerformedExercise(
        req: CreatePerformedExerciseRequest
    ): CreatePerformedExerciseResponse {
        return httpClient.post("/v1/performed-exercises") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()
    }

    @Serializable
    data class GetPerformedExercisesByWorkoutIdRequest(
        val workoutId: String
    )

    @Serializable
    data class GetPerformedExercisesByWorkoutIdResponse(
        @SerialName("performed_exercises")
        val performedExercises: List<RemotePerformedExercise>
    )

    suspend fun getPerformedExercisesByWorkoutId(
        req: GetPerformedExercisesByWorkoutIdRequest
    ): GetPerformedExercisesByWorkoutIdResponse {
        return httpClient.get("/v1/performed-exercises/${req.workoutId}").body()
    }
}
