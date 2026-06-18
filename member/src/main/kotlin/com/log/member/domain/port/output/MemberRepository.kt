package com.log.member.domain.port.output

import com.log.member.domain.model.Member
import com.log.member.domain.model.SocialProvider

interface MemberRepository {

    fun save(member: Member): Member

    fun findById(id: Long): Member?

    fun findByEmail(email: String): Member?

    fun findBySocialAccount(provider: SocialProvider, providerId: String): Member?

    fun existsByNickname(nickName: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findAllByIds(ids: List<Long>): List<Member>
}
