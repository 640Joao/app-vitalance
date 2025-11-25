package com.vitalance.app.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user_streak")
data class UserStreak(

    // ⚙️ ID mapeado pelo relacionamento com User
    @Id
    @Column(name = "user_id")
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Usa o mesmo ID do User
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "current_streak", nullable = false)
    var currentStreak: Int = 0,

    @Column(name = "last_login_date", nullable = false)
    var lastLoginDate: LocalDate = LocalDate.now().minusDays(1),

    // 🧠 Adicionado para controlo de concorrência otimista
    @Version
    var version: Int? = null
)
