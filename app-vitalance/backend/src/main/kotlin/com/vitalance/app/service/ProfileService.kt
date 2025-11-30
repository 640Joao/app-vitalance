package com.vitalance.app.service

import com.vitalance.app.dto.ProfileResponseDTO
import com.vitalance.app.dto.ProfileUpdateDTO
import com.vitalance.app.model.User
import com.vitalance.app.repository.ActivityRepository
import com.vitalance.app.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository
) {

    fun getProfile(userId: Long): ProfileResponseDTO {
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }
        return mapUserToProfileResponse(user)
    }

    @Transactional
    fun updateProfile(userId: Long, updateDTO: ProfileUpdateDTO): ProfileResponseDTO {
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // Atualiza dados básicos
        updateDTO.name?.let { user.username = it }
        updateDTO.bio?.let { user.bio = it }
        updateDTO.profileImage?.let { user.profilePictureUrl = it }

        // --- ATUALIZA AS METAS ---
        // Se o frontend mandou um novo valor, salva no usuário
        updateDTO.weeklyDistanceGoal?.let { user.weeklyDistanceGoal = it }
        updateDTO.weeklyFrequencyGoal?.let { user.weeklyFrequencyGoal = it }

        val updatedUser = userRepository.save(user)
        return mapUserToProfileResponse(updatedUser)
    }

    private fun mapUserToProfileResponse(user: User): ProfileResponseDTO {
        // Busca estatísticas
        val activities = activityRepository.findByUser(user)
        val totalDistance = activities.sumOf { it.distanceKm }
        val totalRuns = activities.size
        val currentStreak = if (activities.isNotEmpty()) 1 else 0

        return ProfileResponseDTO(
            userId = user.id!!,
            email = user.email,
            name = user.username ?: "",
            bio = user.bio,
            profileImage = user.profilePictureUrl,
            totalDistance = totalDistance,
            totalRuns = totalRuns,
            currentStreak = currentStreak,

            // Devolve as metas salvas no banco para exibir na tela
            weeklyDistanceGoal = user.weeklyDistanceGoal,
            weeklyFrequencyGoal = user.weeklyFrequencyGoal
        )
    }
}