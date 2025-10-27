package com.vitalance.backend.service

import com.vitalance.backend.dto.ResetPasswordConfirmationRequest
import com.vitalance.backend.model.PasswordResetToken
import com.vitalance.backend.model.User
import com.vitalance.backend.repository.PasswordResetTokenRepository
import com.vitalance.backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService
) {

    // --- CADASTRO ---
    @Transactional
    fun registerUser(email: String, plainPassword: String): User {
        // 1. Verifica se o usuário já existe
        if (userRepository.findByEmail(email).isPresent) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Usuário com este e-mail já existe.")
        }

        // 2. Criptografa a senha usando BCrypt
        val hashedPassword = passwordEncoder.encode(plainPassword)

        // 3. Cria e salva o novo usuário
        val newUser = User(email = email, password = hashedPassword)
        return userRepository.save(newUser)
    }

    // --- LOGIN ---
    fun login(email: String, plainPassword: String): User {
        val user = userRepository.findByEmail(email).orElseThrow {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.")
        }

        if (!passwordEncoder.matches(plainPassword, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.")
        }

        return user
    }

    // --- RESET DE SENHA - ETAPA 1: SOLICITAÇÃO DE TOKEN ---
    @Transactional
    fun requestPasswordReset(email: String): String {
        val user = userRepository.findByEmail(email).orElse(null)

        if (user != null) {
            // CORREÇÃO CRÍTICA: Exclusão de todos os tokens antigos para evitar conflito no MySQL
            tokenRepository.deleteAllByUser(user)

            // 1. Gera token único e expiração
            val tokenString = UUID.randomUUID().toString()
            val expiryTime = LocalDateTime.now().plusHours(1) // Token válido por 1 hora

            // 2. Salva o novo token
            val resetToken = PasswordResetToken(
                token = tokenString,
                expiryDate = expiryTime,
                user = user
            )
            tokenRepository.save(resetToken)

            // 3. Envia o e-mail
            val resetLink = "http://seufrontend.com/reset?token=$tokenString"
            emailService.sendPasswordResetEmail(user.email, resetLink)

            return "Se o e-mail estiver cadastrado, você receberá um link de redefinição."
        }

        // Resposta genérica para não revelar se o e-mail existe
        return "Se o e-mail estiver cadastrado, você receberá um link de redefinição."
    }

    // --- RESET DE SENHA - ETAPA 2: ATUALIZAÇÃO DA SENHA ---
    @Transactional
    fun resetPassword(token: String, request: ResetPasswordConfirmationRequest): String {
        // Validação de Confirmação de Senha
        if (request.password != request.confirmPassword) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha e a confirmação de senha não coincidem.")
        }

        // 1. Busca o token
        val resetToken = tokenRepository.findByToken(token).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de redefinição inválido.")
        }

        // 2. Verifica a validade
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de redefinição expirado.")
        }

        // 3. Atualiza a senha e salva
        val user = resetToken.user
        val newHashedPassword = passwordEncoder.encode(request.password)
        user.password = newHashedPassword

        userRepository.save(user)
        tokenRepository.delete(resetToken) // Deleta o token após o uso

        return "Senha redefinida com sucesso."
    }
}