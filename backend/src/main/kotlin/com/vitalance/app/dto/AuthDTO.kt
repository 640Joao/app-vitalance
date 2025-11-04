package com.vitalance.app.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

// Expressão regular para forçar complexidade de senha:
const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$"

// --- DTOs COMUNS (Auth) ---

// DTO usado para Cadastro e Login
data class AuthRequest(
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String,

    @field:Pattern(regexp = PASSWORD_REGEX, message = "A senha deve ter no mínimo 6 caracteres e incluir 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo (@$!%*?&).")
    val password: String
)

// DTO para a requisição de Login (usado pelo AuthService)
data class LoginRequestDTO(
    val email: String,
    val password: String
)

// DTO para a resposta de Login (VERSÃO DO SEU COLEGA - CORRETA)
data class AuthResponseDTO(
    val token: String,
    val tokenType: String = "Bearer" // Padrão de mercado
)

// DTO para a requisição inicial de reset (apenas e-mail no corpo JSON)
data class EmailRequest(
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String
)

// DTO para a requisição de reset de senha (com confirmação)
data class ResetPasswordConfirmationRequest(
    @field:Pattern(regexp = PASSWORD_REGEX, message = "A nova senha deve ter no mínimo 6 caracteres e incluir 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo (@$!%*?&).")
    val password: String,

    @field:Pattern(regexp = PASSWORD_REGEX, message = "A senha de confirmação deve ter no mínimo 6 caracteres e incluir 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo (@$!%*?&).")
    val confirmPassword: String
)

// --- DTOs DO SEU PERFIL (Main) ---

// DTO para receber a atualização do perfil (AGORA INCLUI AS CONFIGURAÇÕES)
data class ProfileUpdateDTO(
    // Campos do Perfil
    val username: String?,
    val bio: String?,
    val profilePictureUrl: String?,

    // Campos de Configurações (do seu colega)
    val theme: String?,
    val notificationsEnabled: Boolean?,
    val goal: String?
)

// DTO para enviar os dados do perfil (o que o backend responde)
data class ProfileResponseDTO(
    val userId: Long,
    val email: String,
    val username: String?,
    val bio: String?,
    val profilePictureUrl: String?
)

// --- DTOs GENÉRICOS ---

// DTO de resposta (genérico)
data class MessageResponse(
    val message: String
)

// (Seu DTO 'LoginResponse' foi removido por ser redundante com 'AuthResponseDTO')