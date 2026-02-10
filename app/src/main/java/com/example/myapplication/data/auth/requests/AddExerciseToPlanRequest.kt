package com.example.myapplication.data.auth.requests

data class AddExerciseToPlanRequest(
    val planId: Long,
    val exerciseId: Long,
    val orderIndex: Int,
    val defaultSets: Int,
    val defaultReps: Int,
    val restSeconds: Int
)
