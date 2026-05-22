package com.log.member.domain.model

/**
 * 소셜 계정 Value Object
 */
data class SocialAccount(
    val provider: SocialProvider,
    val providerId: String,
)
