package com.vitalance.app.config

import com.vitalance.app.security.JwtFilter
import com.vitalance.app.service.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder // Importação necessária
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val authService: AuthService,
    private val passwordEncoder: PasswordEncoder // Agora injetamos o PasswordEncoder
) {

    // O Bean do PasswordEncoder foi REMOVIDO daqui

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    // Define o provedor de autenticação
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(authService) // Usa o AuthService
        provider.setPasswordEncoder(passwordEncoder) // Usa o PasswordEncoder injetado
        return provider
    }

    // Configuração principal de segurança (atualizada)
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll() // Rotas públicas
                    .anyRequest().authenticated() // Todas as other são protegidas
            }
            // Define o provedor de autenticação que criamos
            .authenticationProvider(authenticationProvider())
            // Adiciona o filtro JWT
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}