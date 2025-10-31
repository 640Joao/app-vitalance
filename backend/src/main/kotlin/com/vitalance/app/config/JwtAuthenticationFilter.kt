package com.vitalance.app.config

import com.vitalance.app.component.JwtTokenProvider
import com.vitalance.app.service.AuthService // Nosso UserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider,
    private val authService: AuthService // Nosso UserDetailsService
) : OncePerRequestFilter() { // Garante que o filtro rode apenas uma vez por requisição

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // 1. Tenta pegar o token da requisição
            val jwt = getJwtFromRequest(request)

            // 2. Se o token existe e é válido...
            if (jwt != null && tokenProvider.validateToken(jwt)) {
                // 3. Pega o e-mail de dentro do token
                val email = tokenProvider.getEmailFromJWT(jwt)

                // 4. Carrega os detalhes do usuário usando o AuthService
                val userDetails = authService.loadUserByUsername(email)

                // 5. Cria a "Autenticação" (o 'Principal' que estava faltando!)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // 6. Define o usuário como "logado" para esta requisição
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            // Se algo der errado (token expirado, etc.), não fazemos nada.
            // O usuário simplesmente não será autenticado.
            logger.error("Could not set user authentication in security context", ex)
        }

        // 7. Continua a cadeia de filtros (deixa a requisição seguir)
        filterChain.doFilter(request, response)
    }

    // Método auxiliar para extrair o "Bearer eyJ..." do cabeçalho
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length)
        }
        return null
    }
}