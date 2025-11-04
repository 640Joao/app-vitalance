package com.vitalance.app.model

import jakarta.persistence.*

// Define esta classe como uma Entidade JPA, mapeada para uma tabela chamada 'users'
@Entity
@Table(name = "users")
data class User(
    // ID (Idêntico em ambos)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Email (Mesclado - Usando 'var' da branch do seu colega)
    @Column(unique = true, nullable = false)
    var email: String,

    // Password (Mesclado - 'var' é o correto)
    @Column(nullable = false)
    var password: String,

    // --- SEUS CAMPOS DO PERFIL (da branch main) ---
    @Column(unique = true) // Garante que o nome de usuário seja único
    var username: String? = null,

    var bio: String? = null,

    @Column(name = "profile_picture_url")
    var profilePictureUrl: String? = null,

    // --- CAMPOS DELE (da branch feat/settings) ---
    @Column(nullable = false)
    var theme: String = "light", // Valor padrão 'light'

    @Column(nullable = false)
    var notificationsEnabled: Boolean = true, // Valor padrão 'true'

    @Column(nullable = true)
    var goal: String? = null // Meta do usuário (ex: "Correr 5km")
)