package com.vitalance.vitalance.service

import com.vitalance.vitalance.model.User
import com.vitalance.vitalance.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
     fun createUser(user: User): User {
        // Verifica se já existe um usuário com este e-mail
        if (userRepository.findByEmail(user.email) != null) {
            throw IllegalStateException("Usuário com o e-mail ${user.email} já existe.")
        }

        // Cria um novo usuário com a senha codificada
        val newUser = User(
            name = user.name,
            email = user.email,
            // AQUI está a parte crucial: codificamos a senha
            passwordHash = passwordEncoder.encode(user.passwordHash),
            theme = user.theme
        )

        return userRepository.save(newUser)
    }

    /**
     * Busca um usuário pelo ID.
     * Lança uma exceção se o usuário não for encontrado.
     */
    fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow {
            NoSuchElementException("Usuário com ID $id não encontrado.")
        }
    }

    /**
     * Busca um usuário pelo e-mail.
     * Retorna null se o usuário não for encontrado.
     */
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}