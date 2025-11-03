package com.vitalance.app.service

// Imports (Combinação dos seus e dos dele)
import com.vitalance.app.security.JwtTokenProvider
import com.vitalance.app.dto.AuthResponseDTO
import com.vitalance.app.dto.LoginRequestDTO
import com.vitalance.app.dto.ResetPasswordConfirmationRequest
import com.vitalance.app.model.PasswordResetToken
import com.vitalance.app.model.User
import com.vitalance.app.repository.PasswordResetTokenRepository
import com.vitalance.app.repository.UserRepository
import com.vitalance.app.service.EmailService
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority // Importação necessária
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val tokenProvider: JwtTokenProvider
) : UserDetailsService {

    // --- CADASTRO ---
    @Transactional
    fun registerUser(email: String, plainPassword: String): User {
        if (userRepository.findByEmail(email).isPresent) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Usuário com este e-mail já existe.")
        }
        val hashedPassword = passwordEncoder.encode(plainPassword)
        val newUser = User(email = email, password = hashedPassword)
        return userRepository.save(newUser)
    }

    // --- LOGIN (VALIDAÇÃO INTERNA) ---
    fun login(email: String, plainPassword: String): User {
        val user = userRepository.findByEmail(email).orElseThrow {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.")
        }
        if (!passwordEncoder.matches(plainPassword, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.")
        }
        return user
    }

    // --- NOVA FUNÇÃO (DO SEU COLEGA) ---
    fun authenticateAndGenerateToken(loginRequest: LoginRequestDTO): AuthResponseDTO {
        val user = login(loginRequest.email, loginRequest.password)
        val token = tokenProvider.generateToken(user.email)
        return AuthResponseDTO(token = token)
    }

    // --- RESET DE SENHA - ETAPA 1: SOLICITAÇÃO DE TOKEN ---
    @Transactional
    fun requestPasswordReset(email: String): String {
        val user = userRepository.findByEmail(email).orElse(null)
        if (user != null) {
            tokenRepository.deleteAllByUser(user)
            val tokenString = UUID.randomUUID().toString()
            val expiryTime = LocalDateTime.now().plusHours(1)
            val resetToken = PasswordResetToken(token = tokenString, expiryDate = expiryTime, user = user)
            tokenRepository.save(resetToken)
            val resetLink = "http://seufrontend.com/reset?token=$tokenString"
            emailService.sendPasswordResetEmail(user.email, resetLink)
            return "Se o e-mail estiver cadastrado, você receberá um link de redefinição."
        }
        return "Se o e-mail estiver cadastrado, você receberá um link de redefinição."
    }

    // --- RESET DE SENHA - ETAPA 2: ATUALIZAÇÃO DA SENHA ---
    @Transactional
    fun resetPassword(token: String, request: ResetPasswordConfirmationRequest): String {
        if (request.password != request.confirmPassword) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha e a confirmação de senha não coincidem.")
        }
        val resetToken = tokenRepository.findByToken(token).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de redefinição inválido.")
        }
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de redefinição expirado.")
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

    // --- FUNÇÃO DE CARREGAMENTO DO SPRING SECURITY (CORRIGIDA) ---
    @Transactional
    @Throws(ResponseStatusException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado") }

        // CORREÇÃO: Damos ao usuário uma 'ROLE' (autoridade) básica.
        // Sem isso, o Spring Security bloqueia o acesso (403).
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .authorities(authorities) // <-- A MUDANÇA ESTÁ AQUI
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}