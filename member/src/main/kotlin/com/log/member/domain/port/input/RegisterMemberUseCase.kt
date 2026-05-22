package com.log.member.domain.port.input

/**
 * 회원 가입 유스케이스 (입력 포트)
 * adapter(MemberController) -> port(RegisterMemberUseCase) -> service(MemberService)
 */
interface RegisterMemberUseCase {

    fun register(command: RegisterMemberCommand): Long
}

/**
 * 회원 가입 커맨드
 * Controller 의 Request DTO 와 분리해 도메인이 웹 계층에 의존하지 않도록 함
 */
data class RegisterMemberCommand(
    val email: String,
    val nickname: String,
    val provider: String,
    val providerId: String,
)
