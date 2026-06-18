package com.log.member.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FollowJpaRepository : JpaRepository<FollowJpaEntity, Long> {

    fun existsByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Boolean

    @Modifying
    @Query("DELETE FROM FollowJpaEntity f WHERE f.followerId = :followerId AND f.followingId = :followingId")
    fun deleteByFollowerIdAndFollowingId(followerId: Long, followingId: Long)

    @Query("SELECT f.followerId FROM FollowJpaEntity f WHERE f.followingId = :memberId")
    fun findFollowerIdsByMemberId(memberId: Long): List<Long>

    @Query("SELECT f.followingId FROM FollowJpaEntity f WHERE f.followerId = :memberId")
    fun findFollowingIdsByMemberId(memberId: Long): List<Long>
}
