package com.vitalance.app.controller

import com.vitalance.app.dto.*
import com.vitalance.app.model.User // Importe o model
import com.vitalance.app.service.UserService
import org.springframework.http.HttpStatus // Importe o HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder // Importe o SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException // Importe o ResponseStatusException
// Remova 'java.security.Principal'

@RestController
@RequestMapping("/api/settings")
class SettingsController (
    private val userService : UserService
) {

    // Método auxiliar privado (igual ao seu ProfileController)
    private fun getAuthenticatedUser(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return principal as? User ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.")
    }

    // Método auxiliar para pegar o ID de forma segura
    private fun getAuthenticatedUserId(): Long {
        return getAuthenticatedUser().id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID de usuário inválido.")
    }

    @GetMapping
    fun getUserSettings(): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val settings = userService.getUserSettings(userId)
        return ResponseEntity.ok(settings)
    }

    @PutMapping("/theme")
    fun updateTheme(@RequestBody request: ThemeUpdateRequest) : ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updateUser = userService.updateTheme(userId, request.theme)
        return ResponseEntity.ok(updateUser)
    }

    @PutMapping("/notifications")
    fun updateNotifications(@RequestBody request: NotificationSettingRequest) : ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updatedUser = userService.updateNotificationSettings(userId, request.enabled)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/goal")
    fun updateGoal(@RequestBody request: GoalUpdateRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updatedUser = userService.updateGoal(userId, request.goal)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/change-email")
    fun changeEmail(@RequestBody request: ChangeEmailRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updatedUser = userService.changeEmail(userId, request.newEmail, request.currentPassword)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/delete-account")
    fun deleteAccount() : ResponseEntity<Unit> {
        val userId = getAuthenticatedUserId()
        userService.deleteAccount(userId)
        return ResponseEntity.noContent().build()
    }
}