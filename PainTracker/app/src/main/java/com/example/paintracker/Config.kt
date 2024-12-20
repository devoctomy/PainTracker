package com.example.paintracker

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val painCategories: List<PainCategory>
)