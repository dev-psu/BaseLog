package com.log.member.adapter.input.web

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 회원 API 요청/응답 DTO
 * 도메인 모델과 분리해 API 스펙 변경이 도메인에 영향을 주지 않도록 함
 */

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

data class MemberResponse(
    val id: Long,
    val email: String,
    val nickname: String,
)
