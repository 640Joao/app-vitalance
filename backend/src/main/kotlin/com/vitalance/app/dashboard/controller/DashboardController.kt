package com.vitalance.app.dashboard.controller

import com.vitalance.app.dto.DashboardDTO
import com.vitalance.app.dashboard.service.DashboardService
import com.vitalance.app.model.User // Importação necessária
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder // Importação necessária
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

// Controlador da Dashboard (Página Inicial)
@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {

    // Endpoint para carregar todos os dados da tela inicial
    @GetMapping
    fun getDashboardData(): ResponseEntity<DashboardDTO> {
        // 1. OBTÉM O OBJETO USER LOGADO DIRETAMENTE DO CONTEXTO DE SEGURANÇA
        val authentication = SecurityContextHolder.getContext().authentication

        // Verifica se o principal é o objeto User que colocamos no filtro
        val user = authentication.principal as? User ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou sessão inválida.")

        // 2. Usa o ID REAL do usuário logado
        val userId = user.id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID do usuário não disponível.")

        val dashboardData = dashboardService.getDashboardData(userId)
        return ResponseEntity(dashboardData, HttpStatus.OK)
    }
}