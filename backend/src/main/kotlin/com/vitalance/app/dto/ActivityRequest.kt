package com.vitalance.app.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ActivityRequest(
    // REMOVIDO: O userId é obtido do token JWT, não do JSON.
    // @field:NotNull(message = "O ID do usuário não pode ser nulo.")
    // val userId: Long,

    @field:NotNull(message = "O tipo de atividade é obrigatório.")
    val type: String, // Ex: "RUNNING", "WALKING"

    @field:NotNull(message = "A distância é obrigatória.")
    val distanceKm: Double,

    @field:NotNull(message = "A duração é obrigatória.")
    val durationMinutes: Int,

    val caloriesBurned: Int? = null,

    // Opcional: Se a data não for fornecida, o serviço usará LocalDateTime.now()
    val date: LocalDateTime? = null
)