package com.example.paintracker

import kotlinx.serialization.Serializable

@Serializable
data class PainCategory(
    val id: String,
    val displayName: String,
    val description: String,
    @Serializable(with = ColorSerializer::class) val colour: Int
) {
}