package com.log.member.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.SocialLoginResult
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.port.input.AuthUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authUseCase: AuthUseCase) {

    @PostMapping("/social-login")
    fun socialLogin(@RequestBody @Valid request: SocialLoginRequest): ApiResponse<SocialLoginResponse> {
        val provider = parseProvider(request.provider)
        return when (val result = authUseCase.socialLogin(provider, request.accessToken)) {
            is SocialLoginResult.ExistingMember -> ApiResponse.ok(
                SocialLoginResponse(
                    isNewUser = false,
                    accessToken = result.tokenPair.accessToken,
                    refreshToken = result.tokenPair.refreshToken,
                    onboardingToken = null,
                )
            )
            is SocialLoginResult.NewMember -> ApiResponse.ok(
                SocialLoginResponse(
                    isNewUser = true,
                    accessToken = null,
                    refreshToken = null,
                    onboardingToken = result.onboardingToken,
                )
            )
        }
    }

    @PostMapping("/complete-signup")
    fun completeSignup(@RequestBody @Valid request: CompleteSignupRequest): ApiResponse<TokenResponse> {
        val tokenPair = authUseCase.completeSignup(request.onboardingToken, request.nickname, request.favoriteTeam)
        return ApiResponse.ok(TokenResponse(tokenPair.accessToken, tokenPair.refreshToken))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshRequest): ApiResponse<TokenResponse> {
        val tokenPair = authUseCase.refresh(request.refreshToken)
        return ApiResponse.ok(TokenResponse(tokenPair.accessToken, tokenPair.refreshToken))
    }

    @PostMapping("/logout")
    fun logout(@RequestBody @Valid request: RefreshRequest): ApiResponse<Unit> {
        authUseCase.logout(request.refreshToken)
        return ApiResponse(success = true)
    }

    // 카카오 인증 후 리다이렉트를 받아 social-login 처리까지 수행하는 로컬 테스트용 엔드포인트
    @GetMapping("/kakao/callback")
    fun kakaoCallback(@RequestParam code: String): ApiResponse<SocialLoginResponse> {
        return when (val result = authUseCase.socialLogin(SocialProvider.KAKAO, code)) {
            is SocialLoginResult.ExistingMember -> ApiResponse.ok(
                SocialLoginResponse(
                    isNewUser = false,
                    accessToken = result.tokenPair.accessToken,
                    refreshToken = result.tokenPair.refreshToken,
                    onboardingToken = null,
                )
            )
            is SocialLoginResult.NewMember -> ApiResponse.ok(
                SocialLoginResponse(
                    isNewUser = true,
                    accessToken = null,
                    refreshToken = null,
                    onboardingToken = result.onboardingToken,
                )
            )
        }
    }

    private fun parseProvider(value: String): SocialProvider =
        runCatching { SocialProvider.valueOf(value.uppercase()) }
            .getOrElse { throw ErrorCode.UNSUPPORTED_PROVIDER.toException() }
}
