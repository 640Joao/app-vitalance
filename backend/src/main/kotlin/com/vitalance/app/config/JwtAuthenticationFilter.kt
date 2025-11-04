// O pacote está como 'config', conforme sua instrução
package com.vitalance.app.config

import com.vitalance.app.repository.UserRepository
// A importação do Provider (do seu colega).
// VERIFIQUE ESTE CAMINHO! Se ele estiver em 'auth/security', mude o import.
import com.vitalance.app.security.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.optionals.getOrNull

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        val jwt: String?
        val userEmail: String?

        // 1. Extrai o JWT do header
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        jwt = header.substring(7)

        // 2. Tenta extrair o E-MAIL (CORREÇÃO DO NOME DO MÉTODO)
        userEmail = try {
            // Antes (Errado): jwtTokenProvider.getEmailFromJWT(jwt)
            jwtTokenProvider.extractEmail(jwt) // CORRETO (Linha ~33)
        } catch (e: Exception) {
            filterChain.doFilter(request, response)
            return
        }

        // 3. Se o token for válido e o usuário não estiver autenticado
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {

            val userEntity = userRepository.findByEmail(userEmail).getOrNull()

            if (userEntity != null && jwtTokenProvider.validateToken(jwt)) {

                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

                val authentication = UsernamePasswordAuthenticationToken(
                    userEntity, // Coloca o objeto User real
                    null,
                    authorities
                )

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}