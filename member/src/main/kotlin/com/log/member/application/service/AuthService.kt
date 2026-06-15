package com.log.member.application.service

import com.log.common.security.JwtProvider
import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.RefreshToken
import com.log.member.domain.model.TokenPair
import com.log.member.domain.port.input.AuthUseCase
import com.log.member.domain.port.output.MemberRepository
import com.log.member.domain.port.output.RefreshTokenRepository
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
    private val jwtProvider: JwtProvider,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenExpiry: Long,
) : AuthUseCase {

    override fun login(memberId: Long): TokenPair {
        memberRepository.findById(memberId)
            ?: throw ErrorCode.MEMBER_NOT_FOUND.toException()
        return issueTokenPair(memberId)
    }

    override fun refresh(refreshToken: String): TokenPair {
        val stored = refreshTokenRepository.findByToken(refreshToken)
            ?: throw ErrorCode.INVALID_TOKEN.toException()

        if (stored.isExpired()) {
            refreshTokenRepository.deleteByToken(refreshToken)
            throw ErrorCode.EXPIRED_TOKEN.toException()
        }

        // 리프레시 토큰 교체 — 재사용 공격 방지
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
