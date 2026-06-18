package com.log.watchlog.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.member.domain.port.input.FollowUseCase
import com.log.watchlog.domain.port.input.GetFeedUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feed")
class FeedController(
    private val followUseCase: FollowUseCase,
    private val getFeedUseCase: GetFeedUseCase,
) {

    @GetMapping
    fun getFeed(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<WatchLogPageResponse> {
        val memberId = authentication.principal as Long
        val followingIds = followUseCase.getFollowingIds(memberId)
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val result = getFeedUseCase.getFeed(followingIds, pageable)
        return ApiResponse.ok(
            WatchLogPageResponse(
                content = result.content.map { it.toResponse() },
                page = result.number,
                size = result.size,
                totalElements = result.totalElements,
                totalPages = result.totalPages,
                last = result.isLast,
            )
        )
    }
}
