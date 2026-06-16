package com.log.member.domain.model

sealed class SocialLoginResult {
    data class ExistingMember(val tokenPair: TokenPair) : SocialLoginResult()
    data class NewMember(val onboardingToken: String) : SocialLoginResult()
}
