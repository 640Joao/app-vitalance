package com.vitalance.app.repository


import com.vitalance.app.model.UserStreak
import org.springframework.data.jpa.repository.JpaRepository

interface UserStreakRepository : JpaRepository<UserStreak, Long> {
    // A busca por ID (que é o user_id) já é fornecida por JpaRepository
}