package com.vitalance.app.dto

data class UserSettingsResponse(
    val id: Long,
    val email: String,
    val theme: String,
    val notificationsEnabled: Boolean,
    val goal: String?
)

data class ThemeUpdateRequest(val theme: String)
data class NotificationSettingRequest(val enabled: Boolean)
data class GoalUpdateRequest(val goal: String?)


data class ChangeEmailRequest(
    val newEmail: String,
    val currentPassword: String
)


