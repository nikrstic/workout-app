package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class Exercise (
    @SerializedName("exerciseId")
    val exerciseId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("gifUrl")
    val gif: String,
    @SerializedName("targetMuscles")
    val targetMuscles: List<String>,
    @SerializedName("bodyParts")
    val bodyParts: List<String>,
    @SerializedName("equipments")
    val equipments: List<String>,
    @SerializedName("secondaryMuscles")
    val secondaryMuscles: List<String>,
    @SerializedName("instructions")
    val instructions: List<String>

)