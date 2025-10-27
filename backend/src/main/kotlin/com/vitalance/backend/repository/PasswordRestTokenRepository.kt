package com.vitalance.backend.repository

import com.vitalance.backend.model.PasswordResetToken
import com.vitalance.backend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.util.Optional

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {
    // Busca um token pelo valor da string
    fun findByToken(token: String): Optional<PasswordResetToken>

    // Método usado para encontrar o token ativo do usuário (obrigatório pelo findByUser)
    fun findByUser(user: User): Optional<PasswordResetToken>

    // NOVO MÉTODO: Exclui todos os tokens de um usuário.
    // Usamos @Modifying e @Transactional para garantir que a exclusão seja executada no banco.
    @Modifying
    fun deleteAllByUser(user: User)
}