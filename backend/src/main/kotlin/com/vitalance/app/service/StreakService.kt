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

        // 1. Busca o usuário (necessário para o @MapsId se for a primeira vez)
        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("Usuário não encontrado com ID: $userId")
        }

        // 2. Tenta buscar a ofensiva
        val streakOpt = userStreakRepository.findById(userId)

        val streak: UserStreak
        var isNewStreak = false // Flag para sabermos se é a primeira vez

        if (streakOpt.isPresent) {
            // 3a. Se a ofensiva JÁ EXISTE, usamos o objeto gerenciado pelo Hibernate
            streak = streakOpt.get()
        } else {
            // 3b. Se a ofensiva NÃO EXISTE, criamos um novo objeto
            streak = UserStreak(id = userId, user = user)
            isNewStreak = true
        }

        // 4. Verifica a lógica da Ofensiva
        var shouldSave = false

        if (streak.lastLoginDate.isEqual(yesterday)) {
            // Caso 1: Entrou ontem. Incrementa.
            streak.currentStreak += 1
            shouldSave = true
        } else if (streak.lastLoginDate.isBefore(yesterday)) {
            // Caso 2: Não entrou ontem (gap). Zera e começa em 1.
            streak.currentStreak = 1
            shouldSave = true
        } else if (streak.lastLoginDate.isEqual(today)) {
            // Caso 3: Já entrou hoje. Apenas retorna o valor (não salva).
            return streak.currentStreak
        }

        // 5. Salva se for um novo streak ou se a data/contagem mudou
        if (shouldSave || isNewStreak) {
            streak.lastLoginDate = today

            // 'save' aqui funciona como 'merge', atualizando ou inserindo.
            userStreakRepository.save(streak)
        }

        return streak.currentStreak
    }
}