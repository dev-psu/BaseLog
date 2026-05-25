package com.log.member.domain.port.input

import com.log.member.domain.model.Member

/**
 * 회원 조회 유스케이스 (입력 포트)
 * adapter(MemberController) -> port(GetMemberUseCase) -> service(MemberService)
 */
interface GetMemberUseCase {

    fun getById(id: Long): Member
    fun checkEmailAvailable(email: String)
    fun checkNicknameAvailable(nickname: String)
}
