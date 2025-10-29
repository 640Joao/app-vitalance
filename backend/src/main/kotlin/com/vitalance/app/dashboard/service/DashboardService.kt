package com.vitalance.app.dashboard.service

import com.vitalance.app.dto.DashboardDTO
import com.vitalance.app.dto.LastActivityDTO
import com.vitalance.app.dto.QuickReportDTO
import com.vitalance.app.model.Activity
import com.vitalance.app.repository.ActivityRepository
import com.vitalance.app.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class DashboardService(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository
) {

    // Função principal para montar todos os dados do Dashboard
    fun getDashboardData(userId: Long): DashboardDTO {
        // 1. Encontra o usuário
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // --- 2. Define o período (Últimos 7 dias) ---
        val today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)
        val startOfWeek = today.with(DayOfWeek.MONDAY)

        // --- 3. Busca de Dados ---
        val activities = activityRepository.findAllByUserAndDateBetweenOrderByDateDesc(
            user, startOfWeek, LocalDateTime.now()
        )
        val lastActivity = activityRepository.findTopByUserOrderByDateDesc(user).getOrNull()


        // --- 4. Processamento e Cálculos ---
        val weeklyDistanceKm = activities.sumOf { it.distanceKm }
        val weeklyDurationMinutes = activities.sumOf { it.durationMinutes }
        val performanceReport = generateQuickReport(activities)

        // --- 5. Monta o DTO de Resposta ---
        return DashboardDTO(
            userName = user.email.substringBefore("@"),
            activeGoal = "Correr 5K (Meta de Exemplo)",
            weeklyDistanceKm = weeklyDistanceKm,
            weeklyDurationMinutes = weeklyDurationMinutes,
            lastActivity = mapLastActivity(lastActivity),
            performanceGraph = performanceReport
        )
    }

    // Mapeia a entidade Activity para o DTO de última atividade
    private fun mapLastActivity(activity: Activity?): LastActivityDTO? {
        return activity?.let {
            LastActivityDTO(
                type = it.type,
                distanceKm = it.distanceKm,
                durationMinutes = it.durationMinutes,
                date = it.date
            )
        }
    }

    // Função para calcular o relatório rápido (gráfico de pizza)
    private fun generateQuickReport(activities: List<Activity>): QuickReportDTO {
        val typeCounts = activities.groupingBy { it.type }.eachCount()

        return QuickReportDTO(
            totalRuns = typeCounts.getOrDefault("RUNNING", 0).toLong(),
            totalCycles = typeCounts.getOrDefault("CYCLING", 0).toLong(),
            totalWalks = typeCounts.getOrDefault("WALKING", 0).toLong(),
            // Agrupa outras atividades em "other"
            totalOther = typeCounts.filterKeys { it != "RUNNING" && it != "CYCLING" && it != "WALKING" }.values.sum().toLong()
        )
    }
}