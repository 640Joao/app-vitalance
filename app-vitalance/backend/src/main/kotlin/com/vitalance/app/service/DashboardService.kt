package com.vitalance.app.service

import com.vitalance.app.dto.DashboardDTO
import com.vitalance.app.repository.ActivityRepository // Importante para calcular medalhas
import com.vitalance.app.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class DashboardService(
    private val userRepository: UserRepository,
    private val streakService: StreakService,
    private val trainingService: TrainingService,
    private val activityRepository: ActivityRepository // Adicionado para buscar histórico total (Medalhas)
) {

    fun getDashboardData(userId: Long): DashboardDTO {
        // 1. Encontra o usuário
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // 2. OBTÉM A OFENSIVA (Streaks)
        val currentStreak = streakService.checkAndIncrementStreak(userId)

        // 3. OBTÉM O HISTÓRICO RÁPIDO (Últimos 7 dias)
        val end = LocalDateTime.now()
        val start = end.minusDays(7)
        val summary = trainingService.getAggregatedReport(user, start, end)

        // 4. Obtém a última atividade
        val lastActivity = trainingService.getLastActivity(user)

        // 5. --- CÁLCULO DAS MEDALHAS (Gamificação) ---
        // Precisamos buscar o histórico total para saber se o usuário ganhou medalhas
        val allActivities = activityRepository.findByUser(user)
        val totalDistance = allActivities.sumOf { it.distanceKm }
        val totalRuns = allActivities.size

        var medals = 0
        // Regras de Distância
        if (totalDistance >= 10.0) medals++ // Bronze
        if (totalDistance >= 50.0) medals++ // Prata
        if (totalDistance >= 100.0) medals++ // Ouro

        // Regras de Quantidade
        if (totalRuns >= 5) medals++
        if (totalRuns >= 20) medals++

        // Regras de Ofensiva
        if (currentStreak >= 3) medals++
        if (currentStreak >= 7) medals++

        // --- 6. Monta o DTO de Resposta ---
        return DashboardDTO(
            userName = user.username ?: user.email.substringBefore("@"),
            currentStreak = currentStreak,
            lastSevenDaysSummary = summary,
            lastActivity = lastActivity,
            achievementsCount = medals // Campo obrigatório para a tela de Conquistas
        )
    }
}