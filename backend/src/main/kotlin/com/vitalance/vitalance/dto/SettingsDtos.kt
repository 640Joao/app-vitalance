package com.vitalance.vitalance.dto

data class UserSettingsResponse (
    val id: Long,
    val name: String,
    val email: String,
    val theme: String,
    val notificationsEnabled: Boolean,
    val goal: String?
)

data class ThemeUpdateRequest (val theme: String)

data class NotificationsUpdateRequest (val notificationsEnabled: Boolean)

data class GoalUpdateRequest (val goal: String)

data class ChangeEmailRequest (
    val newEmail: String,
    val currentPassword: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class DeleteAccountRequest(
    val currentPassword: String
)
