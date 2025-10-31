package com.vitalance.app.dashboard.service

import com.vitalance.app.dto.DashboardDTO
import com.vitalance.app.dto.LastActivityDTO
import com.vitalance.app.dto.AggregatedReportDTO
import com.vitalance.app.repository.UserRepository
import com.vitalance.app.service.StreakService
import com.vitalance.app.training.service.TrainingService // Importação correta do serviço movido
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class DashboardService(
    private val userRepository: UserRepository,
    private val streakService: StreakService,
    private val trainingService: TrainingService
) {

    fun getDashboardData(userId: Long): DashboardDTO {
        // 1. Encontra o usuário
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // 2. OBTÉM A OFENSIVA (Streaks) e registra o login do dia
        val currentStreak = streakService.checkAndIncrementStreak(userId)


        // 3. OBTÉM O HISTÓRICO RÁPIDO (Últimos 7 dias)
        val end = LocalDateTime.now()
        val start = end.minusDays(7)

        // Chama o TrainingService para obter dados agregados
        val summary = trainingService.getAggregatedReport(user, start, end)

        // 4. Obtém a última atividade para o histórico
        val lastActivity = trainingService.getLastActivity(user)

        // --- 5. Monta o DTO de Resposta com a nova estrutura ---

        return DashboardDTO(
            userName = user.email.substringBefore("@"),
            currentStreak = currentStreak,
            lastSevenDaysSummary = summary,
            lastActivity = lastActivity
        )
    }
}