package com.vitalance.app.config

import com.vitalance.app.auth.security.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter // NOVIDADE: Injeta o filtro JWT
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    // Configuração principal de segurança
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 1. Desabilita o CSRF
            .csrf { it.disable() }

            // 2. Garante que a API é sem estado (STATELESS)
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // 3. Define as regras de autorização de requisições
            .authorizeHttpRequests { auth ->
                auth
                    // Permite acesso público: Login, Cadastro, Reset
                    .requestMatchers("/api/auth/**").permitAll()

                    // OBRIGA JWT: Todas as outras rotas (Dashboard, Activities) exigem autenticação
                    .anyRequest().authenticated()
            }

            // 4. ADICIONA O FILTRO JWT antes do filtro padrão de username/password
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}