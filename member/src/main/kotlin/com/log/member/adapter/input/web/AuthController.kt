package com.log.member.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.member.domain.port.input.AuthUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authUseCase: AuthUseCase) {

    // 소셜 로그인 구현 전 임시 — memberId 직접 입력으로 토큰 발급
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<TokenResponse> {
        val tokenPair = authUseCase.login(request.memberId)
        return ApiResponse.ok(TokenResponse(tokenPair.accessToken, tokenPair.refreshToken))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ApiResponse<TokenResponse> {
        val tokenPair = authUseCase.refresh(request.refreshToken)
        return ApiResponse.ok(TokenResponse(tokenPair.accessToken, tokenPair.refreshToken))
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshRequest): ApiResponse<Unit> {
        authUseCase.logout(request.refreshToken)
        return ApiResponse(success = true)
    }
}
