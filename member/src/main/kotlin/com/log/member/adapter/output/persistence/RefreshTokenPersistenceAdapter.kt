package com.log.member.adapter.output.persistence

import com.log.member.domain.model.RefreshToken
import com.log.member.domain.port.output.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val repository: RefreshTokenJpaRepository,
) : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken) {
        repository.save(
            RefreshTokenJpaEntity(
                token = refreshToken.token,
                memberId = refreshToken.memberId,
                expiresAt = refreshToken.expiresAt,
            )
        )
    }

    override fun findByToken(token: String): RefreshToken? =
        repository.findByToken(token)?.toDomain()

    override fun deleteByToken(token: String) {
        repository.deleteByToken(token)
    }

    override fun deleteByMemberId(memberId: Long) {
        repository.deleteByMemberId(memberId)
    }

    private fun RefreshTokenJpaEntity.toDomain() = RefreshToken(
        token = token,
        memberId = memberId,
        expiresAt = expiresAt,
    )
}
