package com.vitalance.app.service

import com.vitalance.app.security.JwtTokenProvider
import com.vitalance.app.dto.LoginRequestDTO
import com.vitalance.app.dto.AuthResponseDTO
import com.vitalance.app.dto.ResetPasswordConfirmationRequest
import com.vitalance.app.model.PasswordResetToken
import com.vitalance.app.model.User
import com.vitalance.app.repository.PasswordResetTokenRepository
import com.vitalance.app.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val tokenProvider: JwtTokenProvider
) : UserDetailsService {

    @Transactional
    fun registerUser(email: String, plainPassword: String): User {
        if (userRepository.findByEmail(email).isPresent) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Usuário com este e-mail já existe.")
        }
        val hashedPassword = passwordEncoder.encode(plainPassword)
        val newUser = User(email = email, password = hashedPassword)
        return userRepository.save(newUser)
    }

    private fun login(email: String, plainPassword: String): User {
        val user = userRepository.findByEmail(email).orElseThrow {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.")
        }
        if (!passwordEncoder.matches(plainPassword, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.")
        }
        return user
    }

    fun authenticateAndGenerateToken(loginRequest: LoginRequestDTO): AuthResponseDTO {
        val user = login(loginRequest.email, loginRequest.password)
        val token = tokenProvider.generateToken(user.email)
        return AuthResponseDTO(token = token)
    }

    @Transactional
    fun requestPasswordReset(email: String): String {
        val user = userRepository.findByEmail(email).orElse(null)

        if (user == null) {
            return "Se o e-mail estiver cadastrado, você receberá um código de redefinição."
        }

        val code = ThreadLocalRandom.current().nextInt(1000, 10000).toString()
        val expiryTime = LocalDateTime.now().plusMinutes(10)

        tokenRepository.deleteAllByUser(user)

        val resetToken = PasswordResetToken(token = code, expiryDate = expiryTime, user = user)
        tokenRepository.save(resetToken)

        emailService.sendPasswordResetEmail(user.email, code)

        return "Um código de verificação de 4 dígitos foi enviado para o seu email."
    }

    @Transactional
    fun resetPassword(token: String, request: ResetPasswordConfirmationRequest): String {
        val resetToken = tokenRepository.findByToken(token).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido ou expirado.")
        }

        if (request.password != request.confirmPassword) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha e a confirmação de senha não coincidem.")
        }

        if (resetToken.expiryDate.isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado. Solicite um novo.")
        }

        val user = resetToken.user
        if (passwordEncoder.matches(request.password, user.password)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha não pode ser igual à senha anterior.")
        }

        val newHashedPassword = passwordEncoder.encode(request.password)
        user.password = newHashedPassword
        userRepository.save(user)
        tokenRepository.delete(resetToken)

        return "Senha redefinida com sucesso."
    }

    @Transactional
    @Throws(ResponseStatusException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado") }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}