package com.vitalance.app.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,

    // --- NOVOS CAMPOS DO PERFIL ---
    // Tornamos 'var' (mutável) para que possam ser atualizados

    @Column(unique = true) // Garante que o nome de usuário seja único
    var username: String? = null,

    var bio: String? = null,

    @Column(name = "profile_picture_url")
    var profilePictureUrl: String? = null
)