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
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val authService: AuthService,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * AuthenticationManager para autenticação do Spring Security
     */
    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    /**
     * AuthenticationProvider usando UserDetailsService + PasswordEncoder
     */
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(authService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    /**
     * Configuração global de CORS
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://localhost:4173",
            "http://192.168.1.64:4173",
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:8080",
            "http://192.168.2.118:8082",
            "http://192.168.2.118:3000"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    /**
     * Configuração principal do Spring Security
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->

                // 🔓 Rotas públicas
                auth.requestMatchers("/api/auth/reset-password/**").permitAll()
                auth.requestMatchers("/api/auth/**").permitAll()
                auth.requestMatchers("/auth/**").permitAll()

                // 🔓 Swagger
                auth.requestMatchers("/v3/api-docs/**").permitAll()
                auth.requestMatchers("/swagger-ui/**").permitAll()
                auth.requestMatchers("/swagger-ui.html").permitAll()

                // 🔐 Rotas protegidas
                auth.requestMatchers("/api/dashboard/**").authenticated()
                auth.requestMatchers("/api/profile/**").authenticated()
                auth.requestMatchers("/api/activities/**").authenticated()
                auth.requestMatchers("/api/settings/**").authenticated()

                // 🔐 Qualquer outra rota exige autenticação
                auth.anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
