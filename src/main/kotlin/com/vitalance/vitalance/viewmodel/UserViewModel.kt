package com.vitalance.vitalance.viewmodel

import com.vitalance.vitalance.model.User
import com.vitalance.vitalance.service.UserService
import org.springframework.stereotype.Component

@Component
class UserViewModel(private val userService: UserService) {

    fun updateTheme(id: Long, theme: String): User {
        require(theme == "light" || theme == "dark") { "Tema inválido" }
        return userService.updateTheme(id, theme)
    }

    fun updateNotifications(id: Long, enabled: Boolean): User =
        userService.updateNotifications(id, enabled)

    fun updateGoal(id: Long, goal: String): User {
        require(goal.length <= 100) { "Meta muito longa" }
        return userService.updateGoal(id, goal)
    }

    fun changeEmail(id: Long, email: String): User {
        require(email.contains("@")) { "E-mail inválido" }
        return userService.changeEmail(id, email)
    }

    fun changePassword(id: Long, newPassword: String): User {
        require(newPassword.length >= 6) { "Senha deve ter pelo menos 6 caracteres" }
        return userService.changePassword(id, newPassword)
    }

    fun deleteAccount(id: Long) = userService.deleteAccount(id)
}