package com.vitalance.backend.model

import jakarta.persistence.*

// Define esta classe como uma Entidade JPA, mapeada para uma tabela chamada 'users'
@Entity
@Table(name = "users")
data class User(
    // ID gerado automaticamente, é a chave primária
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // O e-mail deve ser único em todo o banco de dados
    @Column(unique = true, nullable = false)
    val email: String,

    // A senha é armazenada como um hash (BCrypt)
    @Column(nullable = false)
    var password: String // 'var' permite que a senha seja atualizada no reset
)