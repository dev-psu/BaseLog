package com.log.member.adapter.output.persistence

import com.log.member.domain.model.SocialProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByEmail(email: String): MemberJpaEntity?
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean

    // @ElementCollection은 메서드명 파생 쿼리가 불가하여 JPQL 직접 작성
    @Query("SELECT m FROM MemberJpaEntity m JOIN m.socialAccounts sa WHERE sa.provider = :provider AND sa.providerId = :providerId")
    fun findBySocialAccount(
        @Param("provider") provider: SocialProvider,
        @Param("providerId") providerId: String,
    ): MemberJpaEntity?

    fun findAllByIdIn(ids: List<Long>): List<MemberJpaEntity>
}
