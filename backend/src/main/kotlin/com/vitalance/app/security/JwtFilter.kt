package com.vitalance.app.security

import com.vitalance.app.util.JwtUtil
import com.vitalance.app.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.optionals.getOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority // Importação necessária

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        val jwt: String?
        val userId: Long?

        // 1. Extrai o JWT do header (Bearer Token)
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7)

            // 2. Tenta extrair o ID do usuário (subject)
            userId = try {
                jwtUtil.extractUserId(jwt)
            } catch (e: Exception) {
                // Se a extração ou validação falhar (expirado, inválido), avança sem autenticar
                filterChain.doFilter(request, response)
                return
            }
        } else {
            filterChain.doFilter(request, response)
            return
        }

        // 3. Se o token for válido e o usuário NÃO estiver autenticado
        if (userId != null && SecurityContextHolder.getContext().authentication == null) {

            // Busca o objeto User pelo ID (evitando buscas desnecessárias)
            val user = userRepository.findById(userId).getOrNull()

            if (user != null && jwtUtil.validateToken(jwt)) {

                // Cria o objeto de autenticação (usando o próprio objeto User como 'Principal')
                // A lista de autoridades (roles) é vazia por enquanto
                val authentication = UsernamePasswordAuthenticationToken(
                    user, null, listOf(SimpleGrantedAuthority("USER")) // Define a role básica
                )

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // Define o usuário no contexto de segurança do Spring
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}