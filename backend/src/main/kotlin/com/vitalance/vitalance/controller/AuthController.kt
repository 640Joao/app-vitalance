package com.vitalance.vitalance.controller

import com.vitalance.vitalance.dto.RegisterRequest
import com.vitalance.vitalance.dto.UserSettingsResponse
import com.vitalance.vitalance.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthController(
    private val userService: UserService
) {
    @PostMapping("/register")
    fun registerUser(@RequestBody request: RegisterRequest): ResponseEntity<UserSettingsResponse> {
        val user = userService.createUser(request)
        // Retorna 201 Created (o padrão para criação de recurso)
        return ResponseEntity.status(201).body(user)
    }
}