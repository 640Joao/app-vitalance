package com.vitalance.vitalance.repository

import com.vitalance.vitalance.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}