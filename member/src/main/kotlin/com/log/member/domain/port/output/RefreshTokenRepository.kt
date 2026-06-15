package com.log.member.domain.port.output

import com.log.member.domain.model.RefreshToken

interface RefreshTokenRepository {
    fun save(refreshToken: RefreshToken)
    fun findByToken(token: String): RefreshToken?
    fun deleteByToken(token: String)
    fun deleteByMemberId(memberId: Long)
}
