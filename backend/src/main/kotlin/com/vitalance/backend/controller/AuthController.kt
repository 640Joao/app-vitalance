package com.vitalance.backend.controller

import com.vitalance.backend.dto.*
import com.vitalance.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    // Rota de Cadastro: POST /api/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: AuthRequest): MessageResponse {
        authService.registerUser(request.email, request.password)
        return MessageResponse("Cadastro realizado com sucesso!")
    }

    // Rota de Login: POST /api/auth/login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(@Valid @RequestBody request: AuthRequest): MessageResponse {
        authService.login(request.email, request.password)
        // FUTURO: Aqui deve ser implementada a lógica para gerar o JWT
        return MessageResponse("Login realizado com sucesso! (TODO: Gerar JWT)")
    }

    // Rota de Solicitação de Reset: POST /api/auth/reset-request (AGORA RECEBE JSON)
    @PostMapping("/reset-request")
    @ResponseStatus(HttpStatus.OK)
    fun requestPasswordReset(@Valid @RequestBody request: EmailRequest): MessageResponse {
        val message = authService.requestPasswordReset(request.email)
        return MessageResponse(message)
    }

    // Rota de Reset de Senha: POST /api/auth/reset-password/{token} (TOKEN NA URL, SENHAS NO JSON)
    @PostMapping("/reset-password/{token}")
    @ResponseStatus(HttpStatus.OK)
    fun resetPassword(
        @PathVariable token: String,
        @Valid @RequestBody request: ResetPasswordConfirmationRequest
    ): MessageResponse {
        val message = authService.resetPassword(token, request)
        return MessageResponse(message)
    }
}