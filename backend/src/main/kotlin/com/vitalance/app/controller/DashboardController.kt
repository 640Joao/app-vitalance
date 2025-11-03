package com.vitalance.app.controller

import com.vitalance.app.dto.DashboardDTO
import com.vitalance.app.service.DashboardService
import com.vitalance.app.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {

    @GetMapping
    fun getDashboardData(): ResponseEntity<DashboardDTO> {
        // Obtém o objeto User logado do contexto de segurança (via JWT)
        val authentication = SecurityContextHolder.getContext().authentication

        val user = authentication.principal as? User ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.")

        val userId = user.id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID do usuário não disponível.")

        val dashboardData = dashboardService.getDashboardData(userId)
        return ResponseEntity(dashboardData, HttpStatus.OK)
    }
}