package com.vitalance.app.training.service

import com.vitalance.app.dto.AggregatedReportDTO
import com.vitalance.app.dto.LastActivityDTO
import com.vitalance.app.model.Activity
import com.vitalance.app.model.User // Importação necessária
import com.vitalance.app.repository.ActivityRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class TrainingService(
    private val activityRepository: ActivityRepository
) {

    // Função para obter o resumo da última atividade
    fun getLastActivity(user: User): LastActivityDTO? {
        val lastActivity = activityRepository.findTopByUserOrderByDateDesc(user).getOrNull()
        return mapLastActivity(lastActivity)
    }

    // Função para obter o relatório agregado de um período
    fun getAggregatedReport(user: User, startDate: LocalDateTime, endDate: LocalDateTime): AggregatedReportDTO {
        val activities = activityRepository.findAllByUserAndDateBetweenOrderByDateDesc(
            user, startDate, endDate
        )
        return aggregateActivities(activities)
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

    // Função para calcular o relatório agregado (distância, duração, contagem)
    private fun aggregateActivities(activities: List<Activity>): AggregatedReportDTO {
        return AggregatedReportDTO(
            totalDistanceKm = activities.sumOf { it.distanceKm },
            totalDurationMinutes = activities.sumOf { it.durationMinutes },
            totalActivities = activities.size.toLong()
        )
    }
}