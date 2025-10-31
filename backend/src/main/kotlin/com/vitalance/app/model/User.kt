package com.vitalance.app.model

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
    var email: String,

    // A senha é armazenada como um hash (BCrypt)
    @Column(nullable = false)
    var password: String,

    // Campos de Configuração
    @Column(nullable = false)
    var theme: String = "light", // Valor padrão 'light'

    @Column(nullable = false)
    var notificationsEnabled: Boolean = true, // Valor padrão 'true'

    @Column(nullable = true) // 'true' = pode ser nulo
    var goal: String? = null // Meta do usuário (ex: "Correr 5km")
)