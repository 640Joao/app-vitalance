package com.vitalance.vitalance.repository

import com.vitalance.vitalance.model.Settings
import org.springframework.data.jpa.repository.JpaRepository

interface SettingsRepository: JpaRepository<Settings, Long> {
    fun findByUserId(userId: Long): Settings?
}