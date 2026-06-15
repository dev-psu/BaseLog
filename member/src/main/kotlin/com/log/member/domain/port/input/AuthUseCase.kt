package com.log.member.domain.port.input

import com.log.member.domain.model.TokenPair

interface AuthUseCase {
    fun login(memberId: Long): TokenPair
    fun refresh(refreshToken: String): TokenPair
    fun logout(refreshToken: String)
}
