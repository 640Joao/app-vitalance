package com.vitalance.vitalance.controller

import com.vitalance.vitalance.dto.*
import com.vitalance.vitalance.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/settings")
class SettingsController(
    private val userService: UserService
) {

    private fun getAuthenticatedUserId(): Long {
        println("Aviso: Usando ID de usuário Fixo (1L) para fins de teste.")
        return 1L
    }

    @GetMapping
    fun getUserSettings(): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val settings = userService.getUserSettings(userId)
        return ResponseEntity.ok(settings)
    }

    @PutMapping("/theme")
    fun updateTheme(@RequestBody request: ThemeUpdateRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updatedUser = userService.updateTheme(userId, request.theme)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/notifications")
    fun updateNotifications(@RequestBody request: NotificationsUpdateRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updatedUser = userService.updateNotificationSettings(userId, request.notificationsEnabled)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/goal")
    fun updateGoal(@RequestBody request: GoalUpdateRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updateUser = userService.updateGoal(userId, request.goal)
        return ResponseEntity.ok(updateUser)
    }

    @PutMapping("/change-email")
    fun changeEmail(@RequestBody request: ChangeEmailRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getAuthenticatedUserId()
        val updatedUser = userService.changeEmail(userId, request.newEmail, request.currentPassword)
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * [PUT] /api/settings/change-password
     * Altera a senha do usuário, exigindo a senha atual.
     */
    @PutMapping("/change-password")
    fun changePassword(@RequestBody request: ChangePasswordRequest): ResponseEntity<Unit> {
        val userId = getAuthenticatedUserId()
        userService.changePassword(userId, request.currentPassword, request.newPassword)
        // 204 No Content é a resposta padrão para sucesso em PUT/DELETE sem retorno de corpo
        return ResponseEntity.noContent().build()
    }

    /**
     * [DELETE] /api/settings/delete-account
     * Exclui a conta do usuário, exigindo a senha atual.
     */
    @DeleteMapping("/delete-account")
    fun deleteAccount(@RequestBody request: DeleteAccountRequest): ResponseEntity<Unit> {
        val userId = getAuthenticatedUserId()
        userService.deleteAccount(userId, request.currentPassword)
        return ResponseEntity.noContent().build()
    }
}