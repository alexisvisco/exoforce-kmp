package com.exoforce.data.domain

import DayMonthYear
import kotlinx.datetime.Clock

// ============================================================================
// USER PREVIEWS
// ============================================================================

val PreviewUserJames = User(
    id = "usr_fjefijewjfwkomgo",
    email = "james@outlook.fr",
    name = "James",
    phoneNumber = "+33123456789",
    weightKg = 70.0f,
    heightCm = 175.0f,
    accessToken = "tok_abcdefg1234567",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    emailVerifiedAt = Clock.System.now(),
    phoneNumberVerifiedAt = Clock.System.now(),
    admin = false,
)

val PreviewUserSarah = User(
    id = "usr_ksdjfklsdjfklsd",
    email = "sarah@gmail.com",
    name = "Sarah",
    phoneNumber = "+33987654321",
    weightKg = 60.0f,
    heightCm = 165.0f,
    accessToken = "tok_xyz9876543",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    emailVerifiedAt = Clock.System.now(),
    phoneNumberVerifiedAt = Clock.System.now(),
    admin = true,
)

// ============================================================================
// EXERCISE CLASSIFICATION PREVIEWS
// ============================================================================

val PreviewClassificationChest = ExerciseClassification(
    id = "cls_001",
    name = "Chest",
    kind = ExerciseClassificationKind.MajorMuscle,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewClassificationBack = ExerciseClassification(
    id = "cls_002",
    name = "Back",
    kind = ExerciseClassificationKind.MajorMuscle,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewClassificationLegs = ExerciseClassification(
    id = "cls_003",
    name = "Legs",
    kind = ExerciseClassificationKind.MajorMuscle,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewClassificationShoulders = ExerciseClassification(
    id = "cls_004",
    name = "Shoulders",
    kind = ExerciseClassificationKind.MajorMuscle,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewClassificationStrength = ExerciseClassification(
    id = "cls_005",
    name = "Strength",
    kind = ExerciseClassificationKind.ExerciseType,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewClassificationCardio = ExerciseClassification(
    id = "cls_006",
    name = "Cardio",
    kind = ExerciseClassificationKind.ExerciseType,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewClassificationPosteriorChain = ExerciseClassification(
    id = "cls_007",
    name = "Posterior Chain",
    kind = ExerciseClassificationKind.MajorFunctionalChain,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

// ============================================================================
// EXERCISE SET PREVIEWS
// ============================================================================

val PreviewSetStandard = ExerciseSet(
    id = "set_001",
    exerciseId = "ex_001",
    position = 1,
    durationPerRepSec = 0,
    restBetweenRepsSec = 0,
    restAfterSetSec = 60,
    totalDurationSec = 0,
    repetitions = 10,
    asManyAsPossibleRepetitions = false,
    asManyAsPossibleDuration = false,
    asManyAsPossibleDistance = false,
    everyMinuteOnTheMinute = false,
    weightKg = 80.0,
    distanceInMeters = 0.0,
    percentage1RM = 0.0,
    holdSizeMillimeters = 0,
    notes = "Focus on form",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewSetAMRAP = ExerciseSet(
    id = "set_002",
    exerciseId = "ex_002",
    position = 1,
    durationPerRepSec = 0,
    restBetweenRepsSec = 0,
    restAfterSetSec = 120,
    totalDurationSec = 300,
    repetitions = 0,
    asManyAsPossibleRepetitions = true,
    asManyAsPossibleDuration = false,
    asManyAsPossibleDistance = false,
    everyMinuteOnTheMinute = false,
    weightKg = 0.0,
    distanceInMeters = 0.0,
    percentage1RM = 0.0,
    holdSizeMillimeters = 0,
    notes = "Max effort in 5 minutes",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewSetEMOM = ExerciseSet(
    id = "set_003",
    exerciseId = "ex_003",
    position = 1,
    durationPerRepSec = 2,
    restBetweenRepsSec = 0,
    restAfterSetSec = 0,
    totalDurationSec = 600,
    repetitions = 10,
    asManyAsPossibleRepetitions = false,
    asManyAsPossibleDuration = false,
    asManyAsPossibleDistance = false,
    everyMinuteOnTheMinute = true,
    weightKg = 50.0,
    distanceInMeters = 0.0,
    percentage1RM = 0.0,
    holdSizeMillimeters = 0,
    notes = "10 minutes EMOM",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)


val PreviewSetCardio = ExerciseSet(
    id = "set_004",
    exerciseId = "ex_004",
    position = 1,
    durationPerRepSec = 0,
    restBetweenRepsSec = 0,
    restAfterSetSec = 60,
    totalDurationSec = 0,
    repetitions = 1,
    asManyAsPossibleRepetitions = false,
    asManyAsPossibleDuration = false,
    asManyAsPossibleDistance = false,
    everyMinuteOnTheMinute = false,
    weightKg = 0.0,
    distanceInMeters = 5000.0,
    percentage1RM = 0.0,
    holdSizeMillimeters = 0,
    notes = "Steady pace",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

val PreviewSetHangboard = ExerciseSet(
    id = "set_005",
    exerciseId = "ex_005",
    position = 1,
    durationPerRepSec = 7,
    restBetweenRepsSec = 3,
    restAfterSetSec = 60 * 3,
    totalDurationSec = 0,
    repetitions = 10,
    asManyAsPossibleRepetitions = false,
    asManyAsPossibleDuration = false,
    asManyAsPossibleDistance = false,
    everyMinuteOnTheMinute = false,
    weightKg = 0.0,
    distanceInMeters = 0.0,
    percentage1RM = 0.0,
    holdSizeMillimeters = 20,
    notes = "Keep shoulders engaged",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now()
)

// ============================================================================
// EXERCISE PREVIEWS
// ============================================================================

val PreviewExerciseBenchPress = Exercise(
    id = "ex_001",
    title = "Barbell Bench Press",
    videoUrl = "https://example.com/videos/bench-press.mp4",
    restAfterExerciseSec = 180,
    userId = "usr_fjefijewjfwkomgo",
    public = true,
    description = "Classic chest exercise targeting pectorals, anterior deltoids, and triceps. Keep your feet flat on the ground and maintain a slight arch in your lower back.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = PreviewUserJames,
    classifications = listOf(PreviewClassificationChest, PreviewClassificationStrength),
    sets = listOf(
        PreviewSetStandard.copy(id = "set_001_1", position = 1, repetitions = 10, weightKg = 60.0),
        PreviewSetStandard.copy(id = "set_001_2", position = 2, repetitions = 8, weightKg = 80.0),
        PreviewSetStandard.copy(id = "set_001_3", position = 3, repetitions = 6, weightKg = 90.0),
    )
)

val PreviewExercisePullUps = Exercise(
    id = "ex_002",
    title = "Pull-ups",
    videoUrl = "https://example.com/videos/pullups.mp4",
    restAfterExerciseSec = 120,
    userId = null,
    public = true,
    description = "Bodyweight exercise for back and biceps. Use full range of motion from dead hang to chin over bar.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = null,
    classifications = listOf(PreviewClassificationBack, PreviewClassificationStrength),
    sets = listOf(PreviewSetAMRAP.copy(exerciseId = "ex_002"))
)

val PreviewExerciseSquat = Exercise(
    id = "ex_003",
    title = "Back Squat",
    videoUrl = "https://example.com/videos/squat.mp4",
    restAfterExerciseSec = 180,
    userId = "usr_fjefijewjfwkomgo",
    public = true,
    description = "Fundamental lower body exercise. Keep chest up and knees tracking over toes. Go below parallel for full range of motion.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = PreviewUserJames,
    classifications = listOf(
        PreviewClassificationLegs,
        PreviewClassificationStrength,
        PreviewClassificationPosteriorChain
    ),
    sets = listOf(
        PreviewSetStandard.copy(
            id = "set_003_1",
            exerciseId = "ex_003",
            position = 1,
            repetitions = 5,
            weightKg = 100.0
        ),
        PreviewSetStandard.copy(
            id = "set_003_2",
            exerciseId = "ex_003",
            position = 2,
            repetitions = 5,
            weightKg = 120.0
        ),
        PreviewSetStandard.copy(
            id = "set_003_3",
            exerciseId = "ex_003",
            position = 3,
            repetitions = 5,
            weightKg = 140.0
        ),
    )
)

val PreviewExerciseDeadlift = Exercise(
    id = "ex_004",
    title = "Conventional Deadlift",
    videoUrl = "https://example.com/videos/deadlift.mp4",
    restAfterExerciseSec = 240,
    userId = "usr_fjefijewjfwkomgo",
    public = true,
    description = "King of posterior chain exercises. Hinge at the hips, keep back neutral, and drive through your heels.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = PreviewUserJames,
    classifications = listOf(
        PreviewClassificationBack,
        PreviewClassificationLegs,
        PreviewClassificationPosteriorChain,
        PreviewClassificationStrength
    ),
    sets = listOf(
        PreviewSetStandard.copy(
            id = "set_004_1",
            exerciseId = "ex_004",
            position = 1,
            repetitions = 5,
            weightKg = 120.0,
            percentage1RM = 70.0
        ),
        PreviewSetStandard.copy(
            id = "set_004_2",
            exerciseId = "ex_004",
            position = 2,
            repetitions = 3,
            weightKg = 160.0,
            percentage1RM = 85.0
        ),
        PreviewSetStandard.copy(
            id = "set_004_3",
            exerciseId = "ex_004",
            position = 3,
            repetitions = 1,
            weightKg = 180.0,
            percentage1RM = 95.0
        ),
    )
)

val PreviewExerciseOverheadPress = Exercise(
    id = "ex_005",
    title = "Standing Overhead Press",
    videoUrl = "https://example.com/videos/overhead-press.mp4",
    restAfterExerciseSec = 120,
    userId = "usr_ksdjfklsdjfklsd",
    public = true,
    description = "Shoulder strength builder. Press the bar straight up, keeping core tight and avoiding excessive back arch.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = PreviewUserSarah,
    classifications = listOf(PreviewClassificationShoulders, PreviewClassificationStrength),
    sets = listOf(
        PreviewSetStandard.copy(
            id = "set_005_1",
            exerciseId = "ex_005",
            position = 1,
            repetitions = 8,
            weightKg = 40.0
        ),
        PreviewSetStandard.copy(
            id = "set_005_2",
            exerciseId = "ex_005",
            position = 2,
            repetitions = 6,
            weightKg = 50.0
        ),
        PreviewSetStandard.copy(
            id = "set_005_3",
            exerciseId = "ex_005",
            position = 3,
            repetitions = 4,
            weightKg = 55.0
        ),
    )
)

val PreviewExerciseRunning = Exercise(
    id = "ex_006",
    title = "Running",
    videoUrl = "https://example.com/videos/running.mp4",
    restAfterExerciseSec = 300,
    userId = null,
    public = true,
    description = "Cardiovascular endurance exercise. Maintain steady pace and focus on breathing rhythm.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = null,
    classifications = listOf(PreviewClassificationCardio),
    sets = listOf(PreviewSetCardio.copy(exerciseId = "ex_006"))
)

val PreviewExerciseRowingMachine = Exercise(
    id = "ex_007",
    title = "Rowing Machine",
    videoUrl = "https://example.com/videos/rowing.mp4",
    restAfterExerciseSec = 180,
    userId = null,
    public = true,
    description = "Full body cardio with emphasis on back and legs. Drive with legs first, then pull with arms.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = null,
    classifications = listOf(PreviewClassificationCardio, PreviewClassificationBack),
    sets = listOf(
        PreviewSetCardio.copy(
            id = "set_007_1",
            exerciseId = "ex_007",
            distanceInMeters = 2000.0,
            totalDurationSec = 480
        )
    )
)

val PreviewExerciseBurpees = Exercise(
    id = "ex_008",
    title = "Burpees",
    videoUrl = "https://example.com/videos/burpees.mp4",
    restAfterExerciseSec = 60,
    userId = null,
    public = true,
    description = "High intensity full body exercise. Drop down, push up, jump up.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = null,
    classifications = listOf(PreviewClassificationCardio),
    sets = listOf(
        PreviewSetEMOM.copy(exerciseId = "ex_008", repetitions = 10, notes = "10 burpees every minute for 10 minutes")
    )
)

val PreviewExerciseHangboard = Exercise(
    id = "ex_009",
    title = "Hangboard Training",
    videoUrl = "https://example.com/videos/hangboard.mp4",
    restAfterExerciseSec = 60,
    userId = null,
    public = true,
    description = "Finger strength exercise using a hangboard. Focus on grip and shoulder engagement.",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    user = null,
    classifications = listOf(PreviewClassificationStrength),
    sets = listOf(
        PreviewSetHangboard.copy(id = "set_005_1", exerciseId = "ex_009", position = 1),
        PreviewSetHangboard.copy(id = "set_005_2", exerciseId = "ex_009", position = 2),
        PreviewSetHangboard.copy(id = "set_005_3", exerciseId = "ex_009", position = 3)
    )
)


// ============================================================================
// PROGRAM PREVIEWS
// ============================================================================

val PreviewProgramStrength = Program(
    id = "prg_001",
    name = "Strength Training Program",
    userId = "usr_fjefijewjfwkomgo",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    workouts = emptyList(),
    user = PreviewUserJames
)

val PreviewProgramHypertrophy = Program(
    id = "prg_002",
    name = "Hypertrophy & Muscle Building",
    userId = "usr_fjefijewjfwkomgo",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    workouts = emptyList(),
    user = PreviewUserJames
)

val PreviewProgramCardio = Program(
    id = "prg_003",
    name = "Cardio & Conditioning",
    userId = "usr_ksdjfklsdjfklsd",
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    workouts = emptyList(),
    user = PreviewUserSarah
)

// ============================================================================
// WORKOUT PREVIEWS
// ============================================================================

val PreviewWorkoutCompleted = Workout(
    id = "wkt_001",
    programId = "prg_001",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = 3600,
    startedAt = Clock.System.now().minus(kotlin.time.Duration.parse("2h")),
    endedAt = Clock.System.now().minus(kotlin.time.Duration.parse("1h")),
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = listOf(PreviewExerciseBenchPress, PreviewExerciseSquat, PreviewExerciseOverheadPress),
    program = PreviewProgramStrength
)

val PreviewWorkoutInProgress = Workout(
    id = "wkt_002",
    programId = "prg_001",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = null,
    startedAt = Clock.System.now().minus(kotlin.time.Duration.parse("30m")),
    endedAt = null,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = listOf(PreviewExerciseDeadlift, PreviewExercisePullUps),
    program = PreviewProgramStrength
)

val PreviewWorkoutScheduled = Workout(
    id = "wkt_003",
    programId = "prg_001",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = null,
    startedAt = null,
    endedAt = null,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = listOf(
        PreviewExerciseSquat,
        PreviewExerciseBenchPress,
        PreviewExercisePullUps,
        PreviewExerciseHangboard
    ),
    program = PreviewProgramStrength
)

val PreviewWorkoutUpperBody = Workout(
    id = "wkt_004",
    programId = "prg_002",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = null,
    startedAt = null,
    endedAt = null,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = listOf(PreviewExerciseBenchPress, PreviewExerciseOverheadPress, PreviewExercisePullUps),
    program = PreviewProgramHypertrophy
)

val PreviewWorkoutLowerBody = Workout(
    id = "wkt_005",
    programId = "prg_002",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = null,
    startedAt = null,
    endedAt = null,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = listOf(PreviewExerciseSquat, PreviewExerciseDeadlift),
    program = PreviewProgramHypertrophy
)

val PreviewWorkoutCardio = Workout(
    id = "wkt_006",
    programId = "prg_003",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = 2700,
    startedAt = Clock.System.now().minus(kotlin.time.Duration.parse("1h")),
    endedAt = Clock.System.now().minus(kotlin.time.Duration.parse("15m")),
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = listOf(PreviewExerciseRunning, PreviewExerciseRowingMachine, PreviewExerciseBurpees),
    program = PreviewProgramCardio
)

val PreviewWorkoutEmpty = Workout(
    id = "wkt_007",
    programId = "prg_001",
    day = DayMonthYear.from(2025, 11, 9),
    durationSec = null,
    startedAt = null,
    endedAt = null,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    exercises = emptyList(),
    program = PreviewProgramStrength
)
