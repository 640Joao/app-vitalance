package com.vitalance.app.config

// (Certifique-se que os caminhos de import estão corretos para sua estrutura de pacotes)
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
    private val passwordEncoder: PasswordEncoder // Injetando o Encoder (do AppConfig)
) {

    // O Bean do PasswordEncoder NÃO ESTÁ AQUI (Está no AppConfig.kt)

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

    // Configuração principal de segurança (MESCLADA)
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // --- BLOCO DE AUTORIZAÇÃO MESCLADO ---
            .authorizeHttpRequests { auth ->
                auth
                    // Suas rotas públicas
                    .requestMatchers("/api/auth/**").permitAll()

                    // Suas rotas protegidas
                    .requestMatchers("/api/dashboard/**").authenticated()
                    .requestMatchers("/api/profile/**").authenticated()
                    .requestMatchers("/api/activities/**").authenticated()

                    // Rota do seu colega (corrigida para ser protegida)
                    .requestMatchers("/api/settings/**").authenticated()

                    // Regra final
                    .anyRequest().authenticated()
            }
            // --- FIM DO BLOCO MESCLADO ---

            // Define o provedor de autenticação que criamos
            .authenticationProvider(authenticationProvider())
            // Adiciona o filtro JWT
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}