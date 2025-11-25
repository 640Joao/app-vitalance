package com.vitalance.app.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(unique = true)
    var username: String? = null,

    var bio: String? = null,

    @Column(name = "profile_picture_url")
    var profilePictureUrl: String? = null,

    @Column(nullable = false)
    var theme: String = "light",

    @Column(nullable = false)
    var notificationsEnabled: Boolean = true,

    @Column(nullable = true)
    var goal: String? = null,

    // --- NOVOS CAMPOS PARA METAS ---
    @Column(nullable = false)
    var weeklyDistanceGoal: Double = 20.0, // Valor padrão: 20km

    @Column(nullable = false)
    var weeklyFrequencyGoal: Int = 4       // Valor padrão: 4 treinos
)