package com.vitalance.app.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey
import io.jsonwebtoken.JwtParser

@Component
class JwtUtil {

    // Injeta a chave secreta do application.properties
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    // Injeta o tempo de expiração do application.properties
    @Value("\${jwt.expiration}")
    private lateinit var expirationTime: String

    // Retorna a chave de assinatura (garantindo que seja um SecretKey)
    private fun getSigningKey(): SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    // --- GERAÇÃO DO TOKEN ---
    fun generateToken(userId: Long): String {
        // CORREÇÃO: Usamos .build() no ClaimsBuilder para obter o objeto Claims
        val claims: Claims = Jwts.claims().setSubject(userId.toString()).build()
        val now = Date()
        val expiryDate = Date(now.time + expirationTime.toLong())

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    // --- VALIDAÇÃO E EXTRAÇÃO ---
    fun extractAllClaims(token: String): Claims {
        // CORREÇÃO: Usa a sintaxe correta do parserBuilder para a versão 0.12.5+
        val parser: JwtParser = Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()

        return parser.parseClaimsJws(token).body
    }

    // Extrai o ID do usuário (subject)
    fun extractUserId(token: String): Long {
        // Usa o operador de não-nulo (!!) após a conversão
        return extractAllClaims(token).subject!!.toLong()
    }

    // Verifica se o token é válido
    fun validateToken(token: String): Boolean {
        return try {
            extractAllClaims(token)
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }
}