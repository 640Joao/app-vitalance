package com.vitalance.app.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

// --- NOVO REGEX SIMPLIFICADO: Mínimo 6 caracteres e pelo menos 1 número ---
const val PASSWORD_REGEX = "^(?=.*\\d).{6,}$"

// --- DTOs DE AUTENTICAÇÃO ---

data class AuthRequest(
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String,

    // Mensagem de erro alterada para refletir a nova regra
    @field:Pattern(regexp = PASSWORD_REGEX, message = "A senha deve ter no mínimo 6 caracteres e incluir pelo menos 1 número.")
    val password: String
)

data class LoginRequestDTO(
    val email: String,
    val password: String
)

data class AuthResponseDTO(
    val token: String,
    val tokenType: String = "Bearer"
)

// --- DTOs DE RECUPERAÇÃO DE SENHA ---

data class EmailRequest(
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String
)

data class ResetPasswordConfirmationRequest(
    // A validação de senha fraca agora usa o novo e mais simples Regex
    @field:Pattern(regexp = PASSWORD_REGEX, message = "A nova senha deve ter no mínimo 6 caracteres e incluir pelo menos 1 número.")
    val password: String,

    @field:Pattern(regexp = PASSWORD_REGEX, message = "A confirmação deve ter no mínimo 6 caracteres e incluir pelo menos 1 número.")
    val confirmPassword: String
)

// --- DTO GENÉRICO ---
data class MessageResponse(
    val message: String
)