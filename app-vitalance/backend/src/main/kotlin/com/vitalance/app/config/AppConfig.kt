package com.vitalance.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class AppConfig {

    // Movemos o PasswordEncoder para cá, quebrando o ciclo
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}