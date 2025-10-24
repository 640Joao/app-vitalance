package com.vitalance.vitalance.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Desabilita o CSRF (essencial para APIs REST e Postman)
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Nossa API não guarda estado (sessão)
            }
            .authorizeHttpRequests { auth ->
                auth
                    // Permite acesso público às rotas de autenticação E de configurações
                    // Observar que essa configuração é TEMPORÁRIA para permitir testes iniciais
                    .requestMatchers("/api/auth/**", "/api/settings/**").permitAll()

                    // Exige autenticação para todas as OUTRAS rotas (que ainda não existem)
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}