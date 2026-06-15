package com.log.member.adapter.input.web

data class LoginRequest(val memberId: Long)

data class RefreshRequest(val refreshToken: String)

data class TokenResponse(val accessToken: String, val refreshToken: String)
