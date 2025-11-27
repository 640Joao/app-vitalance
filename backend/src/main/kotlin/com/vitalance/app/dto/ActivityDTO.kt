package com.vitalance.app.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class ActivityDTO(

    @field:NotBlank(message = "O tipo de atividade é obrigatório.")
    val type: String, // Ex: "RUNNING", "SQUAT"

    // CORREÇÃO: Permite 0.0 para exercícios, mas não negativo
    @field:Min(value = 0, message = "A distância não pode ser negativa.")
    val distanceKm: Double,

    // Duração deve ser pelo menos 1 minuto
    @field:Min(value = 1, message = "A duração deve ser maior que zero.")
    val durationMinutes: Int,

    val date: LocalDateTime? = null
)