package com.example.paintracker

import android.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class PainCategory(
    val id: String,
    val displayName: String,
    val description: String,
    val colour: Int // Using Int to represent color in ARGB format
) {
    fun getColorHex(): String {
        return String.format("#%08X", colour)
    }
}