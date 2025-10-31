package com.vitalance.app.service

import com.vitalance.app.model.User
import com.vitalance.app.model.UserStreak
import com.vitalance.app.repository.UserStreakRepository
import com.vitalance.app.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StreakService(
    private val userStreakRepository: UserStreakRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun checkAndIncrementStreak(userId: Long): Int {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("Usuário não encontrado com ID: $userId")
        }

        val streak = userStreakRepository.findById(userId).orElse(
            UserStreak(id = userId, user = user)
        )

        var shouldSave = false

        if (streak.lastLoginDate.isEqual(yesterday)) {
            streak.currentStreak += 1
            shouldSave = true
        } else if (streak.lastLoginDate.isBefore(yesterday)) {
            streak.currentStreak = 1
            shouldSave = true
        } else if (streak.lastLoginDate.isEqual(today)) {
            // Já atualizou hoje, apenas retorna
            return streak.currentStreak
        }

        if (shouldSave) {
            streak.lastLoginDate = today
            try {
                userStreakRepository.saveAndFlush(streak)
            } catch (e: org.springframework.orm.ObjectOptimisticLockingFailureException) {
                // ⚠️ Proteção contra atualização simultânea
                println("Conflito de versão em UserStreak (ID=$userId). Tentando novamente.")
                return checkAndIncrementStreak(userId)
            }
        }

        return streak.currentStreak
    }

}