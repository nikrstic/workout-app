package com.example.myapplication.data.auth.requests

import com.google.gson.annotations.SerializedName

data class AddExerciseToPlanRequest(
    val planId: Long,
    val exerciseId: Long,
    val orderIndex: Int,
    val defaultSets: Int,
    val defaultReps: Int,
    val restSeconds: Int,
    @SerializedName("gif") val gif: String
)
