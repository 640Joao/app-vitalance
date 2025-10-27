package com.vitalance.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

// Entidade para armazenar o token de reset, sua validade e o usuário associado
@Entity
data class PasswordResetToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // O token que será enviado ao usuário (String longa e única)
    @Column(nullable = false, unique = true)
    val token: String,

    // Data e hora de expiração do token (Ex: 1 hora após a criação)
    @Column(nullable = false)
    val expiryDate: LocalDateTime,

    // CORREÇÃO: Usamos ManyToOne. O token ainda é único por usuário,
    // mas o repositório garante a exclusão do anterior, e o BD não impõe UNIQUE aqui.
    @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    val user: User
) {
    // Método auxiliar para checar se o token ainda é válido
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiryDate)
}