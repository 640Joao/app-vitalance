package com.vitalance.app.controller

import com.vitalance.app.dto.ProfileResponseDTO
import com.vitalance.app.dto.ProfileUpdateDTO
import com.vitalance.app.model.User
import com.vitalance.app.service.ProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/profile") // Rota protegida por JWT
class ProfileController(
    private val profileService: ProfileService
) {

    // Método auxiliar para obter o objeto User logado (do JWT)
    private fun getAuthenticatedUser(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal
        // Faz o 'cast' do Principal (que o JwtFilter injetou) para o nosso objeto User
        return principal as? User ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.")
    }

    // Rota GET: Visualizar o perfil do usuário logado
    @GetMapping
    fun getProfile(): ResponseEntity<ProfileResponseDTO> {
        val user = getAuthenticatedUser()
        val userId = user.id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID de usuário inválido.")

        val profile = profileService.getProfile(userId)
        return ResponseEntity(profile, HttpStatus.OK)
    }

    // Rota PUT: Atualizar o perfil do usuário logado
    @PutMapping
    fun updateProfile(@Valid @RequestBody updateDTO: ProfileUpdateDTO): ResponseEntity<ProfileResponseDTO> {
        val user = getAuthenticatedUser()
        val userId = user.id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID de usuário inválido.")

        val updatedProfile = profileService.updateProfile(userId, updateDTO)
        return ResponseEntity(updatedProfile, HttpStatus.OK)
    }
}