package com.vitalance.app.controller

import com.vitalance.app.dto.ActivityDTO
import com.vitalance.app.dto.MessageResponse
import com.vitalance.app.repository.ActivityRepository
import com.vitalance.app.repository.UserRepository
import com.vitalance.app.model.Activity
import com.vitalance.app.model.User
import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder // Importação necessária

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun recordActivity(@Valid @RequestBody request: ActivityDTO): ResponseEntity<MessageResponse> {

        // 1. OBTÉM O OBJETO USER LOGADO DO CONTEXTO DE SEGURANÇA (via JWT)
        val authentication = SecurityContextHolder.getContext().authentication

        // O principal deve ser o objeto User que colocamos no JwtFilter
        val authenticatedUser = authentication.principal as? User ?:
        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou token inválido.")

        // O userId é extraído do objeto 'authenticatedUser', não do JSON
        val userId = authenticatedUser.id ?:
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID do usuário não disponível no token.")

        // 2. Encontra o usuário (para garantir que ele exista no BD)
        // Usamos o objeto 'authenticatedUser' que já temos
        val userEntity = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")
        }

        // 3. Cria a entidade Activity
        val newActivity = Activity(
            user = userEntity, // Usa o User extraído do JWT
            type = request.type,
            distanceKm = request.distanceKm,
            durationMinutes = request.durationMinutes,
            // Usa a data fornecida ou a data atual
            date = request.date ?: LocalDateTime.now()
        )

        // 4. Salva no banco
        activityRepository.save(newActivity)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(MessageResponse("Atividade registrada com sucesso."))
    }
}