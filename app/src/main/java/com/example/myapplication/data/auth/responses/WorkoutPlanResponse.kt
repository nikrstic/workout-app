package com.example.myapplication.data.auth.responses

data class WorkoutPlanResponse(
    val id:Long,
    val name : String,
    val description : String
)
data class PlanExercisesResponse(
    val id: Long,
    val planId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val orderIndex: Int,
    val defaultSets: Int,
    val defaultReps: Int,
    val restSeconds: Int,
    val gif: String
)
