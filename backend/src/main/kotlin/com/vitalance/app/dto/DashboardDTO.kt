package com.vitalance.app.dto

import java.time.LocalDateTime

// DTO para representar a última atividade registrada
data class LastActivityDTO(
    val type: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val date: LocalDateTime
)

// DTO para dados rápidos do dashboard (para o gráfico pizza)
data class QuickReportDTO(
    val totalRuns: Long,
    val totalCycles: Long,
    val totalWalks: Long,
    val totalOther: Long
)

// DTO principal que a API irá retornar para a tela inicial
data class DashboardDTO(
    val userName: String,
    val activeGoal: String, // Ex: "Correr 5K esta semana"
    val weeklyDistanceKm: Double,
    val weeklyDurationMinutes: Int,
    val lastActivity: LastActivityDTO?, // Pode ser nulo se não houver atividade
    val performanceGraph: QuickReportDTO
)