package com.log.member.adapter.input.web

import com.log.member.domain.model.KboTeam
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SocialLoginRequest(
    @field:NotBlank val provider: String,
    @field:NotBlank val accessToken: String,
)

data class SocialLoginResponse(
    val isNewUser: Boolean,
    val accessToken: String?,
    val refreshToken: String?,
    val onboardingToken: String?,
)

data class CompleteSignupRequest(
    @field:NotBlank val onboardingToken: String,
    @field:NotBlank @field:Size(min = 2, max = 20) val nickname: String,
    val favoriteTeam: KboTeam?,
)

data class RefreshRequest(@field:NotBlank val refreshToken: String)

data class TokenResponse(val accessToken: String, val refreshToken: String)
