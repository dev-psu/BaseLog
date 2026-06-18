package com.log.member.domain.model

import java.time.LocalDateTime

data class Follow(
    val id: Long = 0,
    val followerId: Long,
    val followingId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
