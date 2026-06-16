package com.log.member.domain.model

import java.time.LocalDateTime

/**
 * 회원 도메인 모델 (Aggregate Root)
 */
data class Member(
    val id: Long = 0,
    val email: Email?,
    val nickname: Nickname,
    val role: MemberRole = MemberRole.USER,
    val status: MemberStatus = MemberStatus.ACTIVE,
    val socialAccounts: List<SocialAccount> = emptyList(),
    val profileImageUrl: String? = null,
    val favoriteTeam: KboTeam? = null,
    val bio: String? = null,
    val isPublic: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
