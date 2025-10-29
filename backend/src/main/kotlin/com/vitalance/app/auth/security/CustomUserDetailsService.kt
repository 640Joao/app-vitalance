package com.vitalance.app.auth.security

import com.vitalance.app.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    // CORREÇÃO: O método agora busca pelo EMAIL (String)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email).orElseThrow {
            UsernameNotFoundException("Usuário não encontrado com e-mail: $email")
        }

        // Retorna um objeto UserDetails do Spring Security
        return User(
            user.email, // O username será o email
            user.password, // O hash da senha
            emptyList() // A lista de autoridades (roles), vazia por enquanto
        )
    }
}