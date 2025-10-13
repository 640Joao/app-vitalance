package com.vitalance.vitalance.service

import com.vitalance.vitalance.model.Settings
import com.vitalance.vitalance.repository.SettingsRepository
import com.vitalance.vitalance.repository.UserRepository

class SettingsService(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository
) {

    fun getUserSettings(userId: Long): Settings? {
        return settingsRepository.findByUserId(userId)
    }

    fun updateTheme(userId: Long, theme: String): Settings? {
        val settings = settingsRepository.findByUserId(userId) ?: return null
        settings.theme = theme
        return settingsRepository.save(settings)
    }

    fun toggleNotifications(userId: Long, enabled: Boolean): Settings? {
        val settings = settingsRepository.findByUserId(userId) ?: return null
        settings.notificationsEnabled = enabled
        return settingsRepository.save(settings)
    }

    fun updateDailyGoal(userId: Long, goal: Int): Settings? {
        val settings = settingsRepository.findByUserId(userId) ?: return null
        settings.dailyGoal = goal
        return settingsRepository.save(settings)
    }

    fun changeEmail(userId: Long, newEmail: String): Boolean {
        val user = userRepository.findById(userId).orElse(null) ?: return false
        user.email = newEmail
        userRepository.save(user)
        return true
    }

    fun changePassword(userId: Long, newPassWord: String): Boolean {
        val user = userRepository.findById(userId).orElse(null) ?: return false
        user.password = newPassWord
        userRepository.save(user)
        return true
    }

    fun deleteAccount(userId: Long): Boolean {
        val user = userRepository.findById(userId).orElse(null) ?: return false
        userRepository.delete(user)
        return true
    }
}