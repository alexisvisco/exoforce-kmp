package com.exoforce.data.domain

data class ExerciseEvent(
    val type: ExerciseEventType,
    val kind: ExerciseEventKind,
    val durationSec: Int? = null,
    val setPosition: Int? = null,
    val repetitionNumber: Int? = null,
    val loop: List<ExerciseEvent>? = null
)

enum class ExerciseEventType(val value: String) {
    PREPARE_COUNTDOWN("prepare_countdown"),

    EFFORT("effort"),

    WAIT_EFFORT("wait_effort"),

    REST_SET("rest_set"),

    REST_REP("rest_rep"),

    REST_EXERCISE("rest_exercise"),

    LOOP("loop"),

    ASK_WEIGHT("ask_weight"),

    ASK_HOLD_SIZE("ask_hold_size"),

    ASK_DISTANCE("ask_distance"),

    ASK_REP("ask_rep"),

    ASK_RPE("ask_rpe"),

    ASK_NOTES("ask_notes")
}

enum class ExerciseEventKind(val value: String) {
    PREPARE("prepare"),

    SETS("sets"),

    FINISH("finish")
}

fun Exercise.buildExerciseEvents(countDownSec: Int? = 5): List<ExerciseEvent> {
    val events = mutableListOf<ExerciseEvent>()
    var lastRestSetSec = 0

    val prepareCountdownSec = countDownSec
    events.add(
        ExerciseEvent(
            type = ExerciseEventType.PREPARE_COUNTDOWN,
            kind = ExerciseEventKind.PREPARE,
            durationSec = prepareCountdownSec
        )
    )

    // Process each set
    sets.forEachIndexed { setIndex, set ->
        val setNumber = setIndex + 1

        val effortTypeSet = if (set.durationPerRepSec > 0 || set.totalDurationSec > 0) {
            ExerciseEventType.EFFORT
        } else {
            ExerciseEventType.WAIT_EFFORT
        }

        val durationPerRepSec = if (set.durationPerRepSec > 0 && !set.asManyAsPossibleDuration) {
            set.durationPerRepSec
        } else {
            null
        }

        val restBetweenRepsSec = if (set.restBetweenRepsSec > 0) {
            set.restBetweenRepsSec
        } else {
            null
        }

        val restAfterSetSec = if (set.restAfterSetSec > 0) {
            set.restAfterSetSec
        } else {
            null
        }

        val totalDurationSec = if (set.totalDurationSec > 0) {
            set.totalDurationSec
        } else {
            null
        }

        when {
            set.asManyAsPossibleRepetitions && (set.durationPerRepSec > 0 || set.restBetweenRepsSec > 0) -> {
                val loopEvents = mutableListOf<ExerciseEvent>()


                if (restBetweenRepsSec != null) {
                    loopEvents.add(
                        ExerciseEvent(
                            type = effortTypeSet,
                            kind = ExerciseEventKind.SETS,
                            setPosition = setNumber,
                            durationSec = durationPerRepSec
                        )
                    )
                }

                if (restBetweenRepsSec != null) {
                    loopEvents.add(
                        ExerciseEvent(
                            type = ExerciseEventType.REST_REP,
                            kind = ExerciseEventKind.SETS,
                            durationSec = restBetweenRepsSec,
                            setPosition = setNumber
                        )
                    )
                }

                // parent loop
                events.add(
                    ExerciseEvent(
                        type = ExerciseEventType.LOOP,
                        kind = ExerciseEventKind.SETS,
                        setPosition = setNumber,
                        loop = loopEvents
                    )
                )
            }

            set.asManyAsPossibleRepetitions -> {
                events.add(
                    ExerciseEvent(
                        type = effortTypeSet,
                        kind = ExerciseEventKind.SETS,
                        durationSec = totalDurationSec,
                        setPosition = setNumber
                    )
                )
                events.add(
                    ExerciseEvent(
                        type = ExerciseEventType.ASK_REP,
                        kind = ExerciseEventKind.SETS,
                        setPosition = setNumber
                    )
                )
            }

            set.everyMinuteOnTheMinute && set.totalDurationSec > 0 && (set.repetitions > 0 || set.distanceInMeters > 0) -> {
                events.add(
                    ExerciseEvent(
                        type = effortTypeSet,
                        kind = ExerciseEventKind.SETS,
                        durationSec = totalDurationSec,
                        setPosition = setNumber
                    )
                )

                // only ask for rep, distance will be asked later
                if (set.distanceInMeters == 0.0) {
                    events.add(
                        ExerciseEvent(
                            type = ExerciseEventType.ASK_REP,
                            kind = ExerciseEventKind.SETS,
                            setPosition = setNumber
                        )
                    )
                }
            }

            set.repetitions > 0 && (set.durationPerRepSec > 0 || set.restBetweenRepsSec > 0) -> {
                for (i in 0 until set.repetitions) {
                    val repNumber = i + 1
                    events.add(
                        ExerciseEvent(
                            type = effortTypeSet,
                            kind = ExerciseEventKind.SETS,
                            durationSec = durationPerRepSec,
                            setPosition = setNumber,
                            repetitionNumber = repNumber
                        )
                    )

                    if (restBetweenRepsSec != null && i < set.repetitions - 1) {
                        events.add(
                            ExerciseEvent(
                                type = ExerciseEventType.REST_REP,
                                kind = ExerciseEventKind.SETS,
                                durationSec = restBetweenRepsSec,
                                repetitionNumber = repNumber,
                                setPosition = setNumber
                            )
                        )
                    }
                }
                events.add(
                    ExerciseEvent(
                        type = ExerciseEventType.ASK_REP,
                        kind = ExerciseEventKind.SETS,
                        setPosition = setNumber
                    )
                )
            }

            set.totalDurationSec > 0 -> {
                events.add(
                    ExerciseEvent(
                        type = effortTypeSet,
                        kind = ExerciseEventKind.SETS,
                        durationSec = totalDurationSec,
                        setPosition = setNumber
                    )
                )
            }

            else -> {
                events.add(
                    ExerciseEvent(
                        type = effortTypeSet,
                        kind = ExerciseEventKind.SETS,
                        setPosition = setNumber
                    )
                )
                if (set.repetitions > 1) {
                    events.add(
                        ExerciseEvent(
                            type = ExerciseEventType.ASK_REP,
                            kind = ExerciseEventKind.SETS,
                            setPosition = setNumber
                        )
                    )
                }
            }
        }

        if (set.weightKg != 0.0) {
            events.add(
                ExerciseEvent(
                    type = ExerciseEventType.ASK_WEIGHT,
                    kind = ExerciseEventKind.SETS,
                    setPosition = setNumber
                )
            )
        }
        if (set.holdSizeMillimeters != 0) {
            events.add(
                ExerciseEvent(
                    type = ExerciseEventType.ASK_HOLD_SIZE,
                    kind = ExerciseEventKind.SETS,
                    setPosition = setNumber
                )
            )
        }
        if (set.distanceInMeters != 0.0) {
            events.add(
                ExerciseEvent(
                    type = ExerciseEventType.ASK_DISTANCE,
                    kind = ExerciseEventKind.SETS,
                    setPosition = setNumber
                )
            )
        }

        if (set.restAfterSetSec > 0 && setIndex + 1 < sets.size) {
            events.add(
                ExerciseEvent(
                    type = ExerciseEventType.REST_SET,
                    kind = ExerciseEventKind.SETS,
                    durationSec = restAfterSetSec,
                    setPosition = setNumber
                )
            )
        }

        if (set.restAfterSetSec > 0) {
            lastRestSetSec = set.restAfterSetSec
        }
    }

    events.add(
        ExerciseEvent(
            type = ExerciseEventType.ASK_RPE,
            kind = ExerciseEventKind.FINISH
        )
    )

    events.add(
        ExerciseEvent(
            type = ExerciseEventType.ASK_NOTES,
            kind = ExerciseEventKind.FINISH
        )
    )

    val restAfterExerciseSec = when {
        this.restAfterExerciseSec > 0 -> this.restAfterExerciseSec
        lastRestSetSec > 0 -> lastRestSetSec
        else -> null
    }

    if (restAfterExerciseSec != null) {
        events.add(
            ExerciseEvent(
                type = ExerciseEventType.REST_EXERCISE,
                kind = ExerciseEventKind.FINISH,
                durationSec = restAfterExerciseSec
            )
        )
    }

    return events
}
