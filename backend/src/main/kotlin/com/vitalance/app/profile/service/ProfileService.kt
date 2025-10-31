package com.vitalance.app.profile.service

import com.vitalance.app.dto.ProfileResponseDTO
import com.vitalance.app.dto.ProfileUpdateDTO
import com.vitalance.app.model.User
import com.vitalance.app.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.transaction.annotation.Transactional // Importação necessária

@Service
class ProfileService(
    private val userRepository: UserRepository
) {

    // Função para OBTER o perfil (usada pelo GET)
    fun getProfile(userId: Long): ProfileResponseDTO {
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // Mapeia a Entidade User para o DTO de Resposta
        return mapUserToProfileResponse(user)
    }

    // Função para ATUALIZAR o perfil (usada pelo PUT)
    @Transactional
    fun updateProfile(userId: Long, updateDTO: ProfileUpdateDTO): ProfileResponseDTO {
        // Busca o usuário existente
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // 1. Aplica as mudanças (atualiza os campos 'var' da entidade)
        user.username = updateDTO.username
        user.bio = updateDTO.bio
        user.profilePictureUrl = updateDTO.profilePictureUrl

        // 2. Salva no banco (o JPA/Hibernate atualiza os campos modificados)
        val updatedUser = userRepository.save(user)

        // 3. Retorna o DTO com os dados atualizados
        return mapUserToProfileResponse(updatedUser)
    }

    // Função auxiliar para mapear a Entidade User para o DTO de Resposta
    private fun mapUserToProfileResponse(user: User): ProfileResponseDTO {
        return ProfileResponseDTO(
            userId = user.id!!, // Sabemos que o ID não é nulo se veio do banco
            email = user.email,
            username = user.username,
            bio = user.bio,
            profilePictureUrl = user.profilePictureUrl
        )
    }
}