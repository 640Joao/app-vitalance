package com.vitalance.app.security


import com.vitalance.app.repository.UserRepository
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
class JwtFilter(
    // CORREÇÃO: Removemos o caminho completo, pois ambos estão no pacote 'auth.security'
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

        // 2. Tenta extrair o E-MAIL
        userEmail = try {
            jwtTokenProvider.extractEmail(jwt)
        } catch (e: Exception) {
            filterChain.doFilter(request, response)
            return
        }

        // 3. Se o token for válido e o usuário não estiver autenticado
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {

            // 4. CORREÇÃO CRÍTICA: Buscamos a Entidade User REAL (do model)
            val userEntity = userRepository.findByEmail(userEmail).getOrNull()

            if (userEntity != null && jwtTokenProvider.validateToken(jwt)) {

                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER")) // Damos a ele a permissão

                val authentication = UsernamePasswordAuthenticationToken(
                    userEntity,
                    null,
                    authorities
                )

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // Define o usuário no contexto de segurança
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}