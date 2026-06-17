package com.log.watchlog.adapter.output.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WatchLogJpaRepository : JpaRepository<WatchLogJpaEntity, Long> {

    fun findByMemberIdOrderByCreatedAtDesc(memberId: Long, pageable: Pageable): Page<WatchLogJpaEntity>

    fun findAllByMemberIdOrderByCreatedAtDesc(memberId: Long): List<WatchLogJpaEntity>

    @Query("SELECT w FROM WatchLogJpaEntity w WHERE w.memberId = :memberId AND w.gameSeason = :season ORDER BY w.createdAt DESC")
    fun findAllByMemberIdAndSeason(memberId: Long, season: Short): List<WatchLogJpaEntity>
}
