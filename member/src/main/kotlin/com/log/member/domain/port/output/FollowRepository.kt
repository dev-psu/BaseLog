package com.log.member.domain.port.output

import com.log.member.domain.model.Follow

interface FollowRepository {
    fun save(follow: Follow): Follow
    fun delete(followerId: Long, followingId: Long)
    fun exists(followerId: Long, followingId: Long): Boolean
    fun findFollowerIds(memberId: Long): List<Long>
    fun findFollowingIds(memberId: Long): List<Long>
}
