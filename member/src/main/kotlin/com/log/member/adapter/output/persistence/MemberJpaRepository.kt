package com.log.member.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository

/**
 * MemberRepository JPA
 * adapter(MemberPersistenceAdapter) -> adapter(MemberJpaRepository)
 */

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByEmail(email: String): MemberJpaEntity?
    fun existsNickname(nickName: String): Boolean
    fun existsByEmail(email: String): Boolean

}
