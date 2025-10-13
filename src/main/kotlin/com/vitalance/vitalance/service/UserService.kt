package com.vitalance.vitalance.service

import com.vitalance.vitalance.model.User
import com.vitalance.vitalance.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
     fun updateTheme(id: Long, theme: String): User {
        val user = userRepository.findById(id).orElseThrow { Exception("Usuário não encontrado") }
        user.theme = theme
        return userRepository.save(user)
    }

    fun updateNotifications(id: Long, enabled: Boolean): User {
        val user = userRepository.findById(id).orElseThrow { Exception("Usuário não encontrado") }
        user.notificationsEnabled = enabled
        return userRepository.save(user)
    }

    fun updateGoal(id: Long, goal: String): User {
        val user = userRepository.findById(id).orElseThrow { Exception("Usuário não encontrado") }
        user.goal = goal
        return userRepository.save(user)
    }

    fun changeEmail(id: Long, newEmail: String): User {
        val user = userRepository.findById(id).orElseThrow { Exception("Usuário não encontrado") }
        user.email = newEmail
        return userRepository.save(user)
    }

    fun changePassword(id: Long, newPassword: String): User {
        val user = userRepository.findById(id).orElseThrow { Exception("Usuário não encontrado") }
        user.password = newPassword // (Você deve encriptar isso depois)
        return userRepository.save(user)
    }

    fun deleteAccount(id: Long) {
        userRepository.deleteById(id)
    }
}