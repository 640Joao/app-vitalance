package com.vitalance.app.repository

import com.vitalance.app.model.PasswordResetToken
import com.vitalance.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.Optional

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {

    fun findByToken(token: String): Optional<PasswordResetToken>
    fun findByUser(user: User): Optional<PasswordResetToken>
    @Modifying
    fun deleteAllByUser(user: User)

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiryDate <= :now")
    fun deleteAllExpiredSince(now: LocalDateTime): Int
}