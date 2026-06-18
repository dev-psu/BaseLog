package com.log.member.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.member.domain.port.input.FollowUseCase
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class FollowController(
    private val followUseCase: FollowUseCase,
) {

    @PostMapping("/{id}/follow")
    fun follow(
        @PathVariable id: Long,
        authentication: Authentication,
    ): ApiResponse<Unit> {
        val followerId = authentication.principal as Long
        followUseCase.follow(followerId, id)
        return ApiResponse.ok(Unit)
    }

    @DeleteMapping("/{id}/follow")
    fun unfollow(
        @PathVariable id: Long,
        authentication: Authentication,
    ): ApiResponse<Unit> {
        val followerId = authentication.principal as Long
        followUseCase.unfollow(followerId, id)
        return ApiResponse.ok(Unit)
    }

    @GetMapping("/{id}/followers")
    fun getFollowers(
        @PathVariable id: Long,
    ): ApiResponse<List<MemberResponse>> {
        return ApiResponse.ok(followUseCase.getFollowers(id).map { it.toResponse() })
    }

    @GetMapping("/{id}/following")
    fun getFollowing(
        @PathVariable id: Long,
    ): ApiResponse<List<MemberResponse>> {
        return ApiResponse.ok(followUseCase.getFollowing(id).map { it.toResponse() })
    }
}
