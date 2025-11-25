package com.vitalance.app.dto

import java.time.LocalDateTime

// DTO para dados agregados de desempenho (Histórico Rápido)
data class AggregatedReportDTO(
    val totalDistanceKm: Double,
    val totalDurationMinutes: Int,
    val totalActivities: Long
)

// DTO para representar a última atividade registrada (usado por TrainingService)
data class LastActivityDTO(
    val type: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val date: LocalDateTime
)

// DTO principal que a API irá retornar para a tela inicial
data class DashboardDTO(
    val userName: String,
    val currentStreak: Int, // OFENSIVA
    val lastSevenDaysSummary: AggregatedReportDTO, // Histórico de Desempenho
    val lastActivity: LastActivityDTO?, // Último registro de atividade
    val achievementsCount: Int
)