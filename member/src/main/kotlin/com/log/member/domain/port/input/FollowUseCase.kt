package com.log.member.domain.port.input

import com.log.member.domain.model.Member

interface FollowUseCase {
    fun follow(followerId: Long, followingId: Long)
    fun unfollow(followerId: Long, followingId: Long)
    fun getFollowers(memberId: Long): List<Member>
    fun getFollowing(memberId: Long): List<Member>
    fun getFollowingIds(memberId: Long): List<Long>
    fun isFollowing(followerId: Long, followingId: Long): Boolean
}
