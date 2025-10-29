package com.vitalance.app.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Activity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Relacionamento com o usuário que registrou a atividade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val type: String, // Ex: "RUNNING", "WALKING", "CYCLING"

    @Column(nullable = false)
    val distanceKm: Double,

    // Duração em minutos
    @Column(nullable = false)
    val durationMinutes: Int,

    // Calorias estimadas
    val caloriesBurned: Int? = null,

    @Column(nullable = false)
    val date: LocalDateTime = LocalDateTime.now()
)