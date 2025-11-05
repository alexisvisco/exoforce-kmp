package com.exoforce.data.domain

import DayMonthYear

/*
type Workout struct {
	ID          string             `json:"id"`
	ProgramID   string             `json:"program_id" zog:"program_id"`
	Day         utils.DayMonthYear `json:"day" zog:"day"`
	DurationSec *int               `json:"duration_sec,omitempty" gorm:"column:duration_sec" zog:"duration_sec"`
	StartedAt   *time.Time         `json:"started_at,omitempty" zog:"started_at"`
	EndedAt     *time.Time         `json:"ended_at,omitempty" zog:"ended_at"`
	CreatedAt   time.Time          `json:"created_at"`
	UpdatedAt   time.Time          `json:"updated_at"`

	Exercises []Exercise `gorm:"many2many:workout_exercises;" json:"exercises,omitempty"`
	Program   *Program   `json:"program,omitempty"`
}
 */
data class Workout (
    val id: String,
    val programId: String,
    val day: DayMonthYear,
    val durationSec: Int?,
    val startedAt: String?,
    val endedAt: String?,
    val createdAt: String,
    val updatedAt: String,

    val exercises: List<Exercise> = emptyList(),
    val program: Program? = null
)