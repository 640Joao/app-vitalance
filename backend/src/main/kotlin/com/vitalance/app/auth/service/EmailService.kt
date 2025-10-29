package com.vitalance.app.auth.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value

@Service
class EmailService(private val mailSender: JavaMailSender) {

    // Define o e-mail do remetente (usando a propriedade configurada)
    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    /**
     * Envia o e-mail de redefinição de senha para o usuário.
     * @param toEmail O endereço de e-mail do destinatário.
     * @param token O token de reset gerado.
     */
    fun sendPasswordResetEmail(toEmail: String, token: String) {
        val message = SimpleMailMessage()

        // O assunto do e-mail
        message.subject = "Redefinição de Senha Vitalance"

        // O remetente do e-mail
        message.from = senderEmail

        // Passamos o destinatário
        message.setTo(toEmail)

        // O link de redefinição que o usuário deve clicar.
        val resetLink = "http://localhost:3000/reset-password?token=$token"

        // O corpo do e-mail
        message.text = """
            Prezado(a) usuário(a),

            Você solicitou uma redefinição de senha para a sua conta Vitalance.
            
            Clique no link abaixo para redefinir sua senha. Este link é válido por 1 hora.

            $resetLink

            Se você não solicitou esta redefinição, por favor, ignore este e-mail.

            Atenciosamente,
            Equipe Vitalance
        """.trimIndent()

        // Envia o e-mail (em ambiente real)
        mailSender.send(message)
    }
}
