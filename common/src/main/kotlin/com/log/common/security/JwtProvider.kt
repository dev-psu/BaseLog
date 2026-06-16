package com.log.common.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Base64
import java.util.Date

data class OnboardingClaims(val provider: String, val providerId: String, val email: String?)

@Component
class JwtProvider(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access-token-expiry}") private val accessTokenExpiry: Long,
) {
    private val secretBytes: ByteArray = Base64.getDecoder().decode(secret)
    private val signer: MACSigner = MACSigner(secretBytes)
    private val verifier: MACVerifier = MACVerifier(secretBytes)

    fun generateAccessToken(memberId: Long): String {
        val now = Instant.now()
        val claims = JWTClaimsSet.Builder()
            .subject(memberId.toString())
            .claim(CLAIM_TYPE, TYPE_ACCESS)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(accessTokenExpiry)))
            .build()

        val jwt = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
        jwt.sign(signer)
        return jwt.serialize()
    }

    fun validateAndGetMemberId(token: String): Long {
        val jwt = try {
            SignedJWT.parse(token)
        } catch (e: Exception) {
            throw InvalidTokenException()
        }

        if (!jwt.verify(verifier)) throw InvalidTokenException()

        val claims = jwt.jwtClaimsSet
        if (claims.expirationTime.before(Date())) throw ExpiredTokenException()
        // onboarding 토큰도 동일한 키로 서명되므로 type 클레임으로 Bearer 사용을 차단
        if (claims.getStringClaim(CLAIM_TYPE) != TYPE_ACCESS) throw InvalidTokenException()

        return claims.subject.toLong()
    }

    fun generateOnboardingToken(provider: String, providerId: String, email: String?): String {
        val now = Instant.now()
        val builder = JWTClaimsSet.Builder()
            .claim(CLAIM_TYPE, TYPE_ONBOARDING)
            .claim(CLAIM_PROVIDER, provider)
            .claim(CLAIM_PROVIDER_ID, providerId)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(ONBOARDING_TOKEN_EXPIRY)))
        email?.let { builder.claim(CLAIM_EMAIL, it) }
        val claims = builder.build()

        val jwt = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
        jwt.sign(signer)
        return jwt.serialize()
    }

    fun parseOnboardingToken(token: String): OnboardingClaims {
        val jwt = try {
            SignedJWT.parse(token)
        } catch (e: Exception) {
            throw InvalidTokenException()
        }

        if (!jwt.verify(verifier)) throw InvalidTokenException()

        val claims = jwt.jwtClaimsSet
        if (claims.expirationTime.before(Date())) throw ExpiredTokenException()
        if (claims.getStringClaim(CLAIM_TYPE) != TYPE_ONBOARDING) throw InvalidTokenException()

        return OnboardingClaims(
            provider = claims.getStringClaim(CLAIM_PROVIDER),
            providerId = claims.getStringClaim(CLAIM_PROVIDER_ID),
            email = claims.getStringClaim(CLAIM_EMAIL),
        )
    }

    companion object {
        private const val ONBOARDING_TOKEN_EXPIRY = 600L
        private const val CLAIM_TYPE = "type"
        private const val CLAIM_PROVIDER = "provider"
        private const val CLAIM_PROVIDER_ID = "providerId"
        private const val CLAIM_EMAIL = "email"
        private const val TYPE_ACCESS = "access"
        private const val TYPE_ONBOARDING = "onboarding"
    }
}
