package com.vitalance.app.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank // Novo para string
import jakarta.validation.constraints.NotNull // Usamos para a própria classe
import java.time.LocalDateTime

data class ActivityRequest(
    // userId FOI REMOVIDO (vem do JWT)

    @field:NotBlank(message = "O tipo de atividade é obrigatório.")
    val type: String, // Ex: "RUNNING", "WALKING"

    @field:Min(value = 1, message = "A distância deve ser maior que zero.") // NOVIDADE: Validação de mínimo
    val distanceKm: Double,

    @field:Min(value = 1, message = "A duração deve ser maior que zero.") // NOVIDADE: Validação de mínimo
    val durationMinutes: Int,

    // Este campo pode ser nulo ou zero, então não precisa de @Min.
    val caloriesBurned: Int? = null,

    val date: LocalDateTime? = null
)