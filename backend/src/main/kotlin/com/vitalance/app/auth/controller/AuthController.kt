package com.vitalance.app.auth.controller

import com.vitalance.app.dto.*
import com.vitalance.app.auth.service.AuthService
import com.vitalance.app.util.JwtUtil
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtUtil: JwtUtil
) {

    // Rota de Cadastro: POST /api/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: AuthRequest): MessageResponse {
        authService.registerUser(request.email, request.password)
        return MessageResponse("Cadastro realizado com sucesso!")
    }

    // Rota de Solicitação de Reset: POST /api/auth/reset-request (RECEBE JSON)
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
        @Valid @RequestBody request: ResetPasswordConfirmationRequest
    ): MessageResponse {
        val message = authService.resetPassword(token, request)
        return MessageResponse(message)
    }

    // Rota de Login: POST /api/auth/login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(@Valid @RequestBody request: AuthRequest): LoginResponse {
        val user = authService.login(request.email, request.password)

        // CORREÇÃO: Converte o ID para Long de forma segura, pois ele não pode ser nulo após o login.
        val userId = user.id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID do usuário não encontrado após o login.")

        // Geração do JWT
        val token = jwtUtil.generateToken(userId)

        return LoginResponse(
            token = token,
            userId = userId, // Usa o userId (Long não-nulo) validado
            message = "Login realizado com sucesso. Token gerado."
        )
    }
}