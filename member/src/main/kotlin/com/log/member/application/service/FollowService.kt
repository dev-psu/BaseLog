package com.log.member.application.service

import com.log.member.domain.exception.ErrorCode
import com.log.member.domain.model.Follow
import com.log.member.domain.model.Member
import com.log.member.domain.port.input.FollowUseCase
import com.log.member.domain.port.output.FollowRepository
import com.log.member.domain.port.output.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class FollowService(
    private val followRepository: FollowRepository,
    private val memberRepository: MemberRepository,
) : FollowUseCase {

    override fun follow(followerId: Long, followingId: Long) {
        if (followerId == followingId) throw ErrorCode.CANNOT_FOLLOW_SELF.toException()
        memberRepository.findById(followingId) ?: throw ErrorCode.MEMBER_NOT_FOUND.toException()
        if (followRepository.exists(followerId, followingId)) return
        followRepository.save(
            Follow(followerId = followerId, followingId = followingId, createdAt = LocalDateTime.now())
        )
    }

    override fun unfollow(followerId: Long, followingId: Long) {
        followRepository.delete(followerId, followingId)
    }

    @Transactional(readOnly = true)
    override fun getFollowers(memberId: Long): List<Member> {
        val ids = followRepository.findFollowerIds(memberId)
        if (ids.isEmpty()) return emptyList()
        return memberRepository.findAllByIds(ids)
    }

    @Transactional(readOnly = true)
    override fun getFollowing(memberId: Long): List<Member> {
        val ids = followRepository.findFollowingIds(memberId)
        if (ids.isEmpty()) return emptyList()
        return memberRepository.findAllByIds(ids)
    }

    @Transactional(readOnly = true)
    override fun getFollowingIds(memberId: Long): List<Long> =
        followRepository.findFollowingIds(memberId)

    @Transactional(readOnly = true)
    override fun isFollowing(followerId: Long, followingId: Long): Boolean =
        followRepository.exists(followerId, followingId)
}
