package com.vitalance.app.repository

import com.vitalance.app.model.Activity
import com.vitalance.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface ActivityRepository : JpaRepository<Activity, Long> {

    // Adicionado para o ProfileService
    fun findByUser(user: User): List<Activity>

    // Busca atividades dentro de um período
    fun findAllByUserAndDateBetweenOrderByDateDesc(
        user: User,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Activity>

    // Pega a última atividade
    fun findTopByUserOrderByDateDesc(user: User): Optional<Activity>

    // Busca todas as atividades ordenadas (usado pelo StreakService)
    fun findByUserOrderByDateDesc(user: User): List<Activity>
}