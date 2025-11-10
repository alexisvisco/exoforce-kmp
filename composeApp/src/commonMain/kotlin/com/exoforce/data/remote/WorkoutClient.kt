package com.exoforce.data.remote

import DayMonthYear
import com.exoforce.data.remote.types.RemoteWorkout
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

class WorkoutClient(private val httpClient: HttpClient) {
    @Serializable
    data class GetWorkoutsByDaysRequest(
        val days: List<DayMonthYear>
    )

    @Serializable
    data class GetWorkoutsByDaysResponse(
        val workouts : List<RemoteWorkout>
    )

    suspend fun getWorkoutsByDays(
        req: GetWorkoutsByDaysRequest
    ): GetWorkoutsByDaysResponse {
        return httpClient.get("/v1/workouts/by_days") {
            url {
                parameters.appendAll("days[]", req.days.map { it.toString() })
            }
        }.body()
    }

}
