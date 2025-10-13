package com.vitalance.vitalance.model

import jakarta.persistence.*

@Entity
data class Settings(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    var theme: String = "light",
    var notificationsEnabled: Boolean = true,
    var dailyGoal: Int = 10000,
)
