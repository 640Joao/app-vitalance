package com.vitalance.app.dto

data class ProfileResponseDTO(
    val userId: Long,
    val name: String,
    val email: String,
    val bio: String?,
    val profileImage: String?,
    val totalDistance: Double = 0.0,
    val totalRuns: Int = 0,
    val currentStreak: Int = 0,

    // Campos de meta para leitura
    val weeklyDistanceGoal: Double,
    val weeklyFrequencyGoal: Int
)

data class ProfileUpdateDTO(
    val name: String?,
    val bio: String?,
    val profileImage: String?,

    // Campos de meta para atualização (opcionais)
    val weeklyDistanceGoal: Double?,
    val weeklyFrequencyGoal: Int?
)