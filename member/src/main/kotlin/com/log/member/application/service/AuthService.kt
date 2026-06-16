package com.log.member.application.service

import com.log.common.security.JwtProvider
import com.log.common.security.JwtTokenException
import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.KboTeam
import com.log.member.domain.model.RefreshToken
import com.log.member.domain.model.SocialLoginResult
import com.log.member.domain.model.SocialProvider
import com.log.member.domain.model.TokenPair
import com.log.member.domain.port.input.AuthUseCase
import com.log.member.domain.port.input.RegisterMemberCommand
import com.log.member.domain.port.input.RegisterMemberUseCase
import com.log.member.domain.port.output.MemberRepository
import com.log.member.domain.port.output.RefreshTokenRepository
import com.log.member.domain.port.output.SocialOAuth2Port
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class AuthService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val registerMemberUseCase: RegisterMemberUseCase,
    private val jwtProvider: JwtProvider,
    private val oAuth2Ports: List<SocialOAuth2Port>,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenExpiry: Long,
) : AuthUseCase {

    override fun socialLogin(provider: SocialProvider, code: String): SocialLoginResult {
        val oAuth2Port = oAuth2Ports.find { it.provider == provider }
            ?: throw ErrorCode.UNSUPPORTED_PROVIDER.toException()

        val userInfo = oAuth2Port.getUserInfo(code)

        val existingMember = memberRepository.findBySocialAccount(provider, userInfo.providerId)

        return if (existingMember != null) {
            SocialLoginResult.ExistingMember(issueTokenPair(existingMember.id))
        } else {
            val onboardingToken = jwtProvider.generateOnboardingToken(
                provider = provider.name,
                providerId = userInfo.providerId,
                email = userInfo.email,
            )
            SocialLoginResult.NewMember(onboardingToken)
        }
    }

    override fun completeSignup(onboardingToken: String, nickname: String, favoriteTeam: KboTeam?): TokenPair {
        val claims = try {
            jwtProvider.parseOnboardingToken(onboardingToken)
        } catch (e: JwtTokenException) {
            throw ErrorCode.INVALID_ONBOARDING_TOKEN.toException()
        }

        val memberId = registerMemberUseCase.register(
            RegisterMemberCommand(
                email = claims.email,
                nickname = nickname,
                provider = claims.provider,
                providerId = claims.providerId,
                favoriteTeam = favoriteTeam,
            )
        )
        return issueTokenPair(memberId)
    }

    override fun refresh(refreshToken: String): TokenPair {
        val stored = refreshTokenRepository.findByToken(refreshToken)
            ?: throw ErrorCode.INVALID_TOKEN.toException()

        if (stored.isExpired()) {
            refreshTokenRepository.deleteByToken(refreshToken)
            throw ErrorCode.EXPIRED_TOKEN.toException()
        }

        // 먼저 삭제 후 발급: issueTokenPair 실패 시에도 기존 토큰 재사용 불가
        refreshTokenRepository.deleteByToken(refreshToken)
        return issueTokenPair(stored.memberId)
    }

    override fun logout(refreshToken: String) {
        refreshTokenRepository.deleteByToken(refreshToken)
    }

    private fun issueTokenPair(memberId: Long): TokenPair {
        val accessToken = jwtProvider.generateAccessToken(memberId)
        val newRefreshToken = UUID.randomUUID().toString()
        refreshTokenRepository.save(
            RefreshToken(
                token = newRefreshToken,
                memberId = memberId,
                expiresAt = Instant.now().plusSeconds(refreshTokenExpiry),
            )
        )
        return TokenPair(accessToken, newRefreshToken)
    }
}
