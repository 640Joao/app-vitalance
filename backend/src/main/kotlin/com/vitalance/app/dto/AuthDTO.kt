package com.vitalance.backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

// Expressão regular para forçar complexidade de senha:
const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$"

// DTO usado para Cadastro e Login
data class AuthRequest(
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String,

    @field:Pattern(regexp = PASSWORD_REGEX, message = "A senha deve ter no mínimo 6 caracteres e incluir 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo (@$!%*?&).")
    val password: String
)

// NOVO DTO: Requisição inicial de reset (apenas e-mail no corpo JSON)
data class EmailRequest(
    @field:Email(message = "O e-mail deve ser válido.")
    val email: String
)

// NOVO DTO: Requisição de reset de senha (com confirmação)
data class ResetPasswordConfirmationRequest(
    @field:Pattern(regexp = PASSWORD_REGEX, message = "A nova senha deve ter no mínimo 6 caracteres e incluir 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo (@$!%*?&).")
    val password: String,

    @field:Pattern(regexp = PASSWORD_REGEX, message = "A senha de confirmação deve ter no mínimo 6 caracteres e incluir 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo (@$!%*?&).")
    val confirmPassword: String
)

// DTO de resposta (genérico)
data class MessageResponse(
    val message: String
)