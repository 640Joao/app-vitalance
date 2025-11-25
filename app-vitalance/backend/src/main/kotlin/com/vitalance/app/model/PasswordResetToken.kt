package com.vitalance.app.model

import com.vitalance.app.model.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "password_reset_token")
data class PasswordResetToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Token: Agora aceita o código de 4 dígitos
    @Column(nullable = false, unique = true)
    val token: String,

    @Column(nullable = false)
    val expiryDate: LocalDateTime,

    @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    val user: User
) {
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiryDate)
}