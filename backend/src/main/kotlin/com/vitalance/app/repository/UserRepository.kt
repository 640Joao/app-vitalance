package com.vitalance.app.repository

import com.vitalance.app.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

// JpaRepository fornece métodos CRUD básicos (save, findById, etc.)
interface UserRepository : JpaRepository<User, Long> {
    // Método personalizado para buscar um usuário pelo e-mail
    fun findByEmail(email: String): Optional<User>
}