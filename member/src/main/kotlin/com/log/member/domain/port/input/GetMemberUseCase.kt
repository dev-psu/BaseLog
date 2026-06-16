package com.log.member.domain.port.input

import com.log.member.domain.model.Member

interface GetMemberUseCase {

    fun getById(id: Long): Member
    fun checkEmailAvailable(email: String)
    fun checkNicknameAvailable(nickname: String)
}
