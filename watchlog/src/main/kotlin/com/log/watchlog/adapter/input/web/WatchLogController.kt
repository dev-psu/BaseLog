package com.log.watchlog.adapter.input.web

import com.log.common.response.ApiResponse
import com.log.watchlog.domain.port.input.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/watch-logs")
class WatchLogController(
    private val createWatchLogUseCase: CreateWatchLogUseCase,
    private val updateWatchLogUseCase: UpdateWatchLogUseCase,
    private val deleteWatchLogUseCase: DeleteWatchLogUseCase,
    private val getWatchLogUseCase: GetWatchLogUseCase,
    private val getWatchStatsUseCase: GetWatchStatsUseCase,
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateWatchLogRequest,
        authentication: Authentication,
    ): ApiResponse<WatchLogResponse> {
        val memberId = authentication.principal as Long
        val command = CreateWatchLogCommand(
            memberId = memberId,
            gameId = request.gameId,
            gameSeason = request.gameSeason,
            venue = request.venue,
            watchType = request.watchType,
            cheeringTeam = request.cheeringTeam,
            result = request.result,
            title = request.title,
            content = request.content,
            seatInfo = request.seatInfo?.toDomain(),
            companions = request.companions,
            mood = request.mood,
            cost = request.cost?.toDomain(),
            weather = request.weather?.toDomain(),
            isPublic = request.isPublic,
            images = request.images.map { ImageCommand(it.imageUrl, it.imageType, it.sortOrder) },
        )
        return ApiResponse.ok(createWatchLogUseCase.create(command).toResponse())
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateWatchLogRequest,
        authentication: Authentication,
    ): ApiResponse<WatchLogResponse> {
        val memberId = authentication.principal as Long
        val command = UpdateWatchLogCommand(
            watchLogId = id,
            requesterId = memberId,
            watchType = request.watchType,
            cheeringTeam = request.cheeringTeam,
            result = request.result,
            title = request.title,
            content = request.content,
            seatInfo = request.seatInfo?.toDomain(),
            companions = request.companions,
            mood = request.mood,
            cost = request.cost?.toDomain(),
            weather = request.weather?.toDomain(),
            isPublic = request.isPublic,
            images = request.images.map { ImageCommand(it.imageUrl, it.imageType, it.sortOrder) },
        )
        return ApiResponse.ok(updateWatchLogUseCase.update(command).toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        authentication: Authentication,
    ): ApiResponse<Unit> {
        val memberId = authentication.principal as Long
        deleteWatchLogUseCase.delete(id, memberId)
        return ApiResponse.ok(Unit)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        authentication: Authentication?,
    ): ApiResponse<WatchLogResponse> {
        val requesterId = authentication?.principal as? Long
        return ApiResponse.ok(getWatchLogUseCase.getById(id, requesterId).toResponse())
    }

    @GetMapping("/me")
    fun getMyLogs(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<WatchLogPageResponse> {
        val memberId = authentication.principal as Long
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val result = getWatchLogUseCase.getMyLogs(memberId, pageable)
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

    @GetMapping("/me/stats")
    fun getMyStats(
        authentication: Authentication,
        @RequestParam(required = false) season: Int?,
    ): ApiResponse<WatchStatsResponse> {
        val memberId = authentication.principal as Long
        return ApiResponse.ok(getWatchStatsUseCase.getMyStats(memberId, season).toResponse())
    }
}
