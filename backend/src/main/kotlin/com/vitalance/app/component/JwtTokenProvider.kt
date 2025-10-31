package com.vitalance.app.component

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    // O construtor deve receber as Strings do application.properties,
    // NÃO o SecretKey!
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expiration-ms}") private val jwtExpirationMs: Long
) {

    // AQUI é onde a SecretKey é CRIADA, usando o jwtSecret
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    /**
     * Gera um token JWT para um e-mail de usuário (o "subject" do token).
     */
    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .setSubject(email) // Define o e-mail como o "dono" (subject) do token
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512) // Assina o token
            .compact()
    }

    /**
     * Extrai o e-mail (subject) de um token JWT.
     * (O SecurityConfig precisará disso)
     */
    fun getEmailFromJWT(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    /**
     * Valida a integridade e expiração de um token.
     * (O SecurityConfig precisará disso)
     */
    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken)
            return true
        } catch (ex: Exception) {
            // Logar o erro (ex. MalformedJwtException, ExpiredJwtException, etc.)
        }
        return false
    }
}