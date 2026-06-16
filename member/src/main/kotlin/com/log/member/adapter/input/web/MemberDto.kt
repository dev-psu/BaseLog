package com.log.member.adapter.input.web

import com.log.member.domain.model.KboTeam
import com.log.member.domain.model.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterMemberRequest(
    @field:NotBlank @field:Email
    val email: String,

    @field:NotBlank @field:Size(min = 2, max = 20)
    val nickname: String,

    @field:NotBlank
    val provider: String,

    @field:NotBlank
    val providerId: String,
)

data class UpdateProfileRequest(
    @field:Size(min = 2, max = 20)
    val nickname: String?,

    val profileImageUrl: String?,

    val favoriteTeam: KboTeam?,

    @field:Size(max = 100)
    val bio: String?,

    val isPublic: Boolean?,
)

data class MemberResponse(
    val id: Long,
    val email: String?,
    val nickname: String,
    val profileImageUrl: String?,
    val favoriteTeam: KboTeam?,
    val bio: String?,
    val isPublic: Boolean,
)

fun Member.toResponse() = MemberResponse(
    id = id,
    email = email?.value,
    nickname = nickname.value,
    profileImageUrl = profileImageUrl,
    favoriteTeam = favoriteTeam,
    bio = bio,
    isPublic = isPublic,
)
