package com.example.myapplication.data.auth.requests

data class PlanExerciseRequest(
    val planId: Long,
    val exerciseId: String,
    val orderIndex: Int,
    val defaultSets: Int,
    val defaultReps: Int,
    val restSeconds: Int
)
