package com.log.member.domain.port.input

import com.log.member.domain.model.KboTeam

interface RegisterMemberUseCase {

    fun register(command: RegisterMemberCommand): Long
}

data class RegisterMemberCommand(
    val email: String?,
    val nickname: String,
    val provider: String,
    val providerId: String,
    val favoriteTeam: KboTeam? = null,
)
