package com.example.paintracker.data

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val painCategories: List<PainCategory>
)