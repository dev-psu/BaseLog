package com.log.member.domain.model

import java.time.Instant

data class RefreshToken(
    val token: String,
    val memberId: Long,
    val expiresAt: Instant,
) {
    fun isExpired(): Boolean = expiresAt.isBefore(Instant.now())
}
