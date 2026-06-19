package org.example.data

data class Question(
    val question: String,
    val explanation: String,
    val options: List<Option>,
    val points: Int = 1,
)
