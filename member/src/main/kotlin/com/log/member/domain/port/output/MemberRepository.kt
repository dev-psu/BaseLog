package com.log.member.domain.port.output

import com.log.member.domain.model.Member

/**
 * 회원 저장소 (출력 포트)
 * service(MemberService) -> port(MemberRepository) -> adapter(MemberPersistenceAdapter)
 */
interface MemberRepository {

    fun save(member: Member): Member

    fun findById(id: Long): Member?

    fun findByEmail(email: String): Member?

    fun existsByNickname(nickName: String): Boolean

    fun existsByEmail(email: String): Boolean
}
