package com.vitalance.app.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(private val mailSender: JavaMailSender) : EmailService {

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    override fun sendEmail(message: SimpleMailMessage) {
        mailSender.send(message)
    }

    override fun sendPasswordResetEmail(toEmail: String, token: String) {
        val message = SimpleMailMessage()

        message.subject = "Código de Redefinição de Senha Vitalance"
        message.from = senderEmail
        message.setTo(toEmail)

        val resetLink = "http://localhost:3000/reset-password?token=$token"

        message.text = """
            Prezado(a) usuário(a),

            Você solicitou a redefinição de senha para sua conta Vitalance.
            
            Seu código de verificação é: $token 
            
            Use este código na tela de redefinição do aplicativo. O código é válido por apenas 10 minutos.

            Você também pode clicar no link abaixo:
            $resetLink

            Atenciosamente,
            Equipe Vitalance
        """.trimIndent()

        mailSender.send(message)
    }
}