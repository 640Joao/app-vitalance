package com.vitalance.app.controller

import com.vitalance.app.dto.*
import com.vitalance.app.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: AuthRequest): MessageResponse {
        authService.registerUser(request.email, request.password)
        return MessageResponse("Cadastro realizado com sucesso!")
    }

    @PostMapping("/reset-request")
    @ResponseStatus(HttpStatus.OK)
    fun requestPasswordReset(@Valid @RequestBody request: EmailRequest): MessageResponse {
        val message = authService.requestPasswordReset(request.email)
        return MessageResponse(message)
    }

    // Rota de Reset de Senha: POST /api/auth/reset-password/{token}
    @PostMapping("/reset-password/{token}")
    @ResponseStatus(HttpStatus.OK)
    fun resetPassword(
        @PathVariable token: String,
        @RequestBody request: ResetPasswordConfirmationRequest
    ): MessageResponse {
        try {
            val message = authService.resetPassword(token, request)
            return MessageResponse(message)
        } catch (e: ResponseStatusException) {
            // CORREÇÃO: Usamos o método getStatusCode() e getReason() que são públicos.
            // O getStatus() retorna o HttpStatus que tem o valor (ex: 400).
            val status = e.statusCode
            val reason = e.reason ?: "Erro desconhecido."

            throw ResponseStatusException(status, reason)
        } catch (e: Exception) {
            // Erro de servidor
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a redefinição.")
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(@Valid @RequestBody loginRequest: LoginRequestDTO): AuthResponseDTO {
        return authService.authenticateAndGenerateToken(loginRequest)
    }
}