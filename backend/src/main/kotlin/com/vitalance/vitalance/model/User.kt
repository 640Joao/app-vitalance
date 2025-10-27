package com.vitalance.vitalance.model

import jakarta.persistence.*


@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String = "",

    // Pode modificar esse campo para a lógica de login
    @Column(nullable = false)
    var passwordHash: String = "",

    var theme: String = "light",  // light ou dark
    var notificationsEnabled: Boolean = true,
    var goal: String? = null,     // meta do usuário (pode ser nula)

    // Dados de auditoria (opcional)
    var createdAt: String? = null,
    var updatedAt: String? = null
)