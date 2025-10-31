package com.vitalance.app.repository

import com.vitalance.app.model.PasswordResetToken
import com.vitalance.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.Optional

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {

    // Busca um token pelo valor da string
    fun findByToken(token: String): Optional<PasswordResetToken>

    // Método usado para encontrar o token ativo do usuário
    fun findByUser(user: User): Optional<PasswordResetToken>

    // Exclui todos os tokens de um usuário (usado no AuthService para unicidade)
    @Modifying
    fun deleteAllByUser(user: User)

    // --- MÉTODO PARA DELETAR TOKENS EXPIRADOS (USADO PELO JOB AGENDADO) ---

    @Modifying // Indica que esta query modifica o banco de dados (DELETE)
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiryDate <= :now")
    fun deleteAllExpiredSince(now: LocalDateTime): Int
}