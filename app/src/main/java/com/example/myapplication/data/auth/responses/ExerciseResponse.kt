package com.example.myapplication.data.auth.responses

import com.example.myapplication.data.model.Exercise
import com.google.gson.annotations.SerializedName

data class ExerciseResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<Exercise>,
    @SerializedName("metadata")
    val metadata: ExerciseMetadata
)

data class ExerciseMetadata(
    val totalExercise: Int,
    val totalPages: Int,
    val currentPage: Int
)