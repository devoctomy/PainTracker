package com.example.paintracker.data

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    var patientName: String = "John Doe",
    var patientSex: Sex = Sex.Male,
    val painCategories: List<PainCategory>
)