package com.vitalance.app.service

import org.springframework.mail.SimpleMailMessage

interface EmailService {
    fun sendEmail(message: SimpleMailMessage)
    fun sendPasswordResetEmail(toEmail: String, token: String)
}