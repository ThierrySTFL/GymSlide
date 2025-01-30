package com.example.slidegym.data.model

data class Exercise(
    val id: Long = 0,
    val name: String,
    val weight: Double,
    val reps: Int,
    val isCompleted: Boolean = false
)