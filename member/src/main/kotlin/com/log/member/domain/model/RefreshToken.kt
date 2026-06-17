package com.log.member.domain.model

import java.time.LocalDateTime

data class RefreshToken(
    val token: String,
    val memberId: Long,
    val expiresAt: LocalDateTime,
) {
    fun isExpired(): Boolean = expiresAt.isBefore(LocalDateTime.now())
}
