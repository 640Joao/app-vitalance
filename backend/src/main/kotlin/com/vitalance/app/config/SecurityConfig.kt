package com.vitalance.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    // Define o Bean que será usado para criptografar e verificar senhas (BCrypt)
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    // Configuração básica de segurança para Spring Security 6
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Desabilita o CSRF (necessário para APIs REST)
            .csrf { it.disable() }

            // Define as regras de autorização de requisições
            .authorizeHttpRequests { auth ->
                auth
                    // Permite acesso público total aos endpoints de autenticação
                    .requestMatchers("/api/auth/**").permitAll()

                    // Exige autenticação para qualquer outra requisição
                    .anyRequest().authenticated()
            }

        // REMOVIDO: Configurações para acesso ao H2 Console
        // Se você precisar que o Spring Security não interfira em rotas internas do servidor, adicione esta linha:
        // .headers { headers -> headers.frameOptions { it.sameOrigin() } }

        return http.build()
    }
}