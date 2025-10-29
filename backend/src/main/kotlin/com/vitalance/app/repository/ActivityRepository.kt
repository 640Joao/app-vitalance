package com.vitalance.app.repository

import com.vitalance.app.model.Activity
import com.vitalance.app.model.User // IMPORTAÇÃO CORRIGIDA
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.Optional

interface ActivityRepository : JpaRepository<Activity, Long> {

    // Busca as atividades de um usuário dentro de um período
    fun findAllByUserAndDateBetweenOrderByDateDesc(
        user: User,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Activity>

    // Encontra a atividade mais recente de um usuário
    fun findTopByUserOrderByDateDesc(user: User): Optional<Activity>

    // Query para calcular a soma das distâncias de um usuário em um período
    @Query("SELECT SUM(a.distanceKm) FROM Activity a WHERE a.user = :user AND a.date >= :startDate")
    fun sumDistanceByUserAndStartDate(user: User, startDate: LocalDateTime): Double?
}