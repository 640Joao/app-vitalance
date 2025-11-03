package com.vitalance.app.security


import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private lateinit var expirationTime: String

    private fun getSigningKey(): SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(email: String): String {
        val claims: Claims = Jwts.claims().setSubject(email).build()
        val now = Date()
        val expiryDate = Date(now.time + expirationTime.toLong())

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun extractEmail(token: String): String {
        return extractAllClaims(token).subject
    }

    fun extractAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
        return parser.parseClaimsJws(token).body
    }

    fun validateToken(token: String): Boolean {
        try {
            extractAllClaims(token)
            return !isTokenExpired(token)
        } catch (e: Exception) {
            return false
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }
}