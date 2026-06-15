package com.log.member.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, Long> {

    fun findByToken(token: String): RefreshTokenJpaEntity?

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.token = :token")
    fun deleteByToken(token: String)

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.memberId = :memberId")
    fun deleteByMemberId(memberId: Long)
}
