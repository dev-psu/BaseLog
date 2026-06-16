package com.log.member.domain.port.input

import com.log.member.domain.model.KboTeam
import com.log.member.domain.model.Member

interface UpdateProfileUseCase {

    fun updateProfile(command: UpdateProfileCommand): Member
}

data class UpdateProfileCommand(
    val memberId: Long,
    val nickname: String?,
    val profileImageUrl: String?,
    val favoriteTeam: KboTeam?,
    val bio: String?,
    val isPublic: Boolean?,
)
