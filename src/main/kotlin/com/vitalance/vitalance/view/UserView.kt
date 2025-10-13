package com.vitalance.vitalance.view

import com.vitalance.vitalance.model.User
import com.vitalance.vitalance.service.UserService
import com.vitalance.vitalance.viewmodel.UserViewModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserView (private val userViewModel: UserViewModel) {

    @PutMapping("/{id}/theme")
    fun updateTheme(@PathVariable id: Long, @RequestParam theme: String): User =
        userViewModel.updateTheme(id, theme)

    @PutMapping("/{id}/notifications")
    fun updateNotifications(@PathVariable id: Long, @RequestParam enabled: Boolean): User =
        userViewModel.updateNotifications(id, enabled)

    @PutMapping("/{id}/goal")
    fun updateGoal(@PathVariable id: Long, @RequestParam goal: String): User =
        userViewModel.updateGoal(id, goal)

    @PutMapping("/{id}/email")
    fun changeEmail(@PathVariable id: Long, @RequestParam email: String): User =
        userViewModel.changeEmail(id, email)

    @PutMapping("/{id}/password")
    fun changePassword(@PathVariable id: Long, @RequestParam newPassword: String): User =
        userViewModel.changePassword(id, newPassword)

    @DeleteMapping("/{id}")
    fun deleteAccount(@PathVariable id: Long) =
        userViewModel.deleteAccount(id)
}