package com.log.member.adapter.input.web

/**
 * 회원 API 요청/응답 DTO
 * 도메인 모델과 분리해 API 스펙 변경이 도메인에 영향을 주지 않도록 함
 */

data class RegisterMemberRequest(
    val email: String,
    val nickname: String,
    val provider: String,
    val providerId: String,
)

data class MemberResponse(
    val id: Long,
    val email: String,
    val nickname: String,
)
