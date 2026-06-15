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

        return claims.subject.toLong()
    }
}
