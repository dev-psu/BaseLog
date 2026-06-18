package com.log.member.adapter.output.persistence

import com.log.member.domain.model.Follow
import com.log.member.domain.port.output.FollowRepository
import org.springframework.stereotype.Component

@Component
class FollowPersistenceAdapter(
    private val followJpaRepository: FollowJpaRepository,
) : FollowRepository {

    override fun save(follow: Follow): Follow =
        followJpaRepository.save(FollowJpaEntity.fromDomain(follow)).toDomain()

    override fun delete(followerId: Long, followingId: Long) =
        followJpaRepository.deleteByFollowerIdAndFollowingId(followerId, followingId)

    override fun exists(followerId: Long, followingId: Long): Boolean =
        followJpaRepository.existsByFollowerIdAndFollowingId(followerId, followingId)

    override fun findFollowerIds(memberId: Long): List<Long> =
        followJpaRepository.findFollowerIdsByMemberId(memberId)

    override fun findFollowingIds(memberId: Long): List<Long> =
        followJpaRepository.findFollowingIdsByMemberId(memberId)
}
