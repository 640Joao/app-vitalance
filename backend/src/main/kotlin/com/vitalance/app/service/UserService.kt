package com.vitalance.app.service

import com.vitalance.app.dto.UserSettingsResponse // Importe seu DTO de resposta
import com.vitalance.app.model.User
import com.vitalance.app.repository.UserRepository

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.* // Para NoSuchElementException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder // Injetado do SecurityConfig
) {

    /**
     * Método auxiliar privado para buscar um usuário pelo ID.
     * Lança uma exceção se não for encontrado, garantindo que nunca seja nulo.
     */
    private fun findUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { NoSuchElementException("Usuário com ID $id não encontrado") }
    }

    /**
     * Método auxiliar privado para converter (mapear) uma entidade User
     * em um DTO UserSettingsResponse seguro, omitindo a senha.
     */
    private fun mapToUserSettingsResponse(user: User): UserSettingsResponse {
        return UserSettingsResponse(
            id = user.id!!, // O ID não será nulo pois o usuário foi encontrado
            email = user.email,
            theme = user.theme,
            notificationsEnabled = user.notificationsEnabled,
            goal = user.goal
        )
    }

    // --- Início: Lógica para o SettingsController ---

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

    /**
     * Atualiza a preferência de notificação do usuário.
     */
    fun updateNotificationSettings(userId: Long, enabled: Boolean): UserSettingsResponse {
        val user = findUserById(userId)
        user.notificationsEnabled = enabled
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }

    /**
     * Atualiza a meta do usuário.
     */
    fun updateGoal(userId: Long, newGoal: String?): UserSettingsResponse {
        val user = findUserById(userId)
        user.goal = newGoal // Permite nulo para limpar a meta
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }

    fun findUserByEmail(email: String): User {
    // Usa o 'findByEmail' que já existe no seu 'UserRepository'
    // .orElseThrow lida exatamente com a lógica que você quer!
    return userRepository.findByEmail(email)
        .orElseThrow { NoSuchElementException("Usuário com e-mail $email não encontrado.") }
}

    /**
     * Altera o e-mail do usuário.
     * Regra de Negócio: Exige a senha atual para confirmação.
     * Regra de Negócio: O novo e-mail não pode estar em uso.
     */
    fun changeEmail(userId: Long, newEmail: String, currentPassword: String): UserSettingsResponse {
        val user = findUserById(userId)

        // Regra 1: Verificar a senha atual
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            throw IllegalArgumentException("Senha atual incorreta.")
        }

        // Regra 2: Verificar se o novo e-mail já está em uso
        if (userRepository.existsByEmail(newEmail)) {
            throw IllegalStateException("O e-mail $newEmail já está em uso por outra conta.")
        }

        user.email = newEmail
        val savedUser = userRepository.save(user)
        return mapToUserSettingsResponse(savedUser)
    }

    /**
     * Exclui a conta do usuário.
     * (Seu controller não pedia a senha, então este método é direto.
     * É UMA BOA PRÁTICA pedir a senha aqui também, como no changeEmail)
     */
    fun deleteAccount(userId: Long) {
        // Validação de segurança:
        // Você deve modificar seu SettingsController e DTO para também
        // pedir a senha atual antes de deletar a conta.

        val user = findUserById(userId)
        userRepository.delete(user)
    }
}