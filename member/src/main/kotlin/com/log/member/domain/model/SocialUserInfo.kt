package com.log.member.domain.model

data class SocialUserInfo(
    val provider: SocialProvider,
    val providerId: String,
    val email: String?,
)
