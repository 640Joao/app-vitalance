package com.vitalance.app.component // Recomendo um pacote 'component'

import com.vitalance.app.repository.PasswordResetTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PasswordTokenCleanupJob(
    private val passwordResetTokenRepository: PasswordResetTokenRepository
) {

    // Roda a cada 6 horas (21.600.000 milissegundos)
    // Isso garante que tokens expirados sejam removidos regularmente.
    @Scheduled(fixedRate = 21600000)
    @Transactional // Garante que a operação de deleção seja atômica
    fun cleanExpiredPasswordResetTokens() {
        val now = LocalDateTime.now()
        // Chama o método no repositório para deletar tokens onde expiryDate <= agora
        val deletedCount = passwordResetTokenRepository.deleteAllExpiredSince(now)

        println("🧹 Limpeza de Tokens de Senha: $deletedCount tokens expirados deletados até $now.")
    }
}