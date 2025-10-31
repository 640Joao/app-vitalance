package com.vitalance.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {



    // Configuração básica de segurança para Spring Security 6
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Desabilita o CSRF (necessário para APIs REST)
            .csrf { it.disable() }

            // Define as regras de autorização de requisições
            .authorizeHttpRequests { auth ->
                auth
                    // Permite acesso público total aos endpoints de autenticação e H2 Console
                    // Permite acesso público total aos endpoints de autenticação, H2 E Configurações
                    .requestMatchers("/api/auth/**", "/h2-console/**", "/api/settings/**").permitAll()

                    // Exige autenticação para qualquer outra requisição
                    .anyRequest().authenticated()

                // Diz ao Spring para rodar seu filtro de token ANTES do filtro de login padrão.
                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            }

            // Configurações para acesso ao H2 Console
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }

        return http.build()
    }
}