package com.vitalance.app.controller

import com.vitalance.app.dto.*
import com.vitalance.app.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/settings")
class SettingsController (
    private val userService : UserService
) {

    private fun getUserIdFromPrincipal(principal: Principal): Long {
        val user = userService.findUserByEmail(principal.name)
        return user.id!!
    }

    @GetMapping
    fun getUserSettings(principal: Principal): ResponseEntity<UserSettingsResponse> {
        val userId = getUserIdFromPrincipal(principal)
        val settings = userService.getUserSettings(userId)
        return ResponseEntity.ok(settings)
    }

    @PutMapping("/theme")
    fun updateTheme(principal: Principal, @RequestBody request: ThemeUpdateRequest) : ResponseEntity<UserSettingsResponse> {
        val userId = getUserIdFromPrincipal(principal)
        val updateUser = userService.updateTheme(userId, request.theme)
        return ResponseEntity.ok(updateUser)
    }

    @PutMapping("/notifications")
    fun updateNotifications(principal: Principal, @RequestBody request: NotificationSettingRequest) : ResponseEntity<UserSettingsResponse> {
        val userId = getUserIdFromPrincipal(principal)
        val updatedUser = userService.updateNotificationSettings(userId, request.enabled)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/goal")
    fun updateGoal(principal: Principal, @RequestBody request: GoalUpdateRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getUserIdFromPrincipal(principal)
        val updatedUser = userService.updateGoal(userId, request.goal)
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/change-email")
    fun changeEmail(principal: Principal, @RequestBody request: ChangeEmailRequest): ResponseEntity<UserSettingsResponse> {
        val userId = getUserIdFromPrincipal(principal)
        val updatedUser = userService.changeEmail(userId, request.newEmail, request.currentPassword)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/delete-account")
    fun deleteAccount(principal: Principal) : ResponseEntity<Unit> {
        val userId = getUserIdFromPrincipal(principal)
        userService.deleteAccount(userId)
        return ResponseEntity.noContent().build()
    }
}