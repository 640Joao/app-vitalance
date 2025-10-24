package com.vitalance.vitalance.service

import com.vitalance.vitalance.dto.RegisterRequest
import com.vitalance.vitalance.dto.UserSettingsResponse
import com.vitalance.vitalance.model.User
import com.vitalance.vitalance.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
     fun createUser(request: RegisterRequest): UserSettingsResponse {
        // Regra: Verificar se o e-mail já existe
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalStateException("Usuário com o e-mail ${request.email} já existe.")
        }

        // Regra: Senha deve ter pelo menos 6 caracteres
        if (request.password.length < 6) {
            throw IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.")
        }

        val newUser = User(
            name = request.name,
            email = request.email,
            // Regra: Codificar a senha ANTES de salvar
            passwordHash = passwordEncoder.encode(request.password),
            // Valores padrão já definidos no Model (theme='light', notifications=true)
        )

        val savedUser = userRepository.save(newUser)
        return mapToUserSettingsResponse(savedUser) // Retorna o DTO seguro
    }

    fun findUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow {
            NoSuchElementException("Usuário com ID $id não encontrado.")
        }
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    private fun mapToUserSettingsResponse(user: User): UserSettingsResponse {
        return UserSettingsResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            theme = user.theme,
            notificationsEnabled = user.notificationsEnabled,
            goal = user.goal
        )
    }

    // --- Lógica de Negócio para Configurações ---

    /**
     * Busca as configurações atuais do usuário.
     */
    fun getUserSettings(userId: Long): UserSettingsResponse {
        val user = findUserById(userId)
        return mapToUserSettingsResponse(user)
    }

    /**
     * Atualiza o tema do usuário.
     * Regra de Negócio: O tema só pode ser 'light' ou 'dark'.
     */
    fun updateTheme(userId: Long, newTheme: String): UserSettingsResponse {
        val user = findUserById(userId)

        if (newTheme !in listOf("light", "dark")) {
            throw IllegalArgumentException("Tema inválido. Use 'light' ou 'dark'.")
        }

        user.theme = newTheme
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }


    // Atualiza a preferência de notificação do usuário.

    fun updateNotificationSettings(userId: Long, enabled: Boolean): UserSettingsResponse {
        val user = findUserById(userId)
        user.notificationsEnabled = enabled
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }


    //  Atualiza a meta do usuário.

    fun updateGoal(userId: Long, newGoal: String?): UserSettingsResponse {
        val user = findUserById(userId)
        user.goal = newGoal // Permite nulo para limpar a meta
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }

    // --- Lógica de Negócio para Configurações Avançadas ---

    fun changeEmail(userId: Long, newEmail: String, currentPassword: String): UserSettingsResponse {
        val user = findUserById(userId)

        if (!passwordEncoder.matches(currentPassword, user.passwordHash)) {
            throw IllegalArgumentException("Senha atual incorreta.")
        }

        if (userRepository.findByEmail(newEmail) != null) {
            throw IllegalStateException("O e-mail $newEmail já está em uso por outra conta.")
        }

        user.email = newEmail
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }


    fun changePassword(userId: Long, currentPassword: String, newPassword: String) {
        val user = findUserById(userId)

        if (!passwordEncoder.matches(currentPassword, user.passwordHash)) {
            throw IllegalArgumentException("Senha atual incorreta.")
        }

        if (newPassword.length < 6) {
            throw IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.")
        }

        user.passwordHash = passwordEncoder.encode(newPassword) // Codifica a nova senha
        userRepository.save(user)
    }

    fun deleteAccount(userId: Long, currentPassword: String) {
        val user = findUserById(userId)

        if (!passwordEncoder.matches(currentPassword, user.passwordHash)) {
            throw IllegalArgumentException("Senha atual incorreta.")
        }

        // Aqui você pode adicionar mais lógicas, como deletar as corridas, etc.
        userRepository.delete(user)
    }
}