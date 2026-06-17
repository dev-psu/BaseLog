package com.log.watchlog.application.service

import com.log.watchlog.domain.exception.WatchLogErrorCode
import com.log.watchlog.domain.model.*
import com.log.watchlog.domain.port.input.*
import com.log.watchlog.domain.port.output.WatchLogRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class WatchLogService(
    private val watchLogRepository: WatchLogRepository,
) : CreateWatchLogUseCase,
    UpdateWatchLogUseCase,
    DeleteWatchLogUseCase,
    GetWatchLogUseCase,
    GetWatchStatsUseCase {

    override fun create(command: CreateWatchLogCommand): WatchLog {
        val now = LocalDateTime.now()
        val watchLog = WatchLog(
            memberId = command.memberId,
            gameId = command.gameId,
            gameSeason = command.gameSeason,
            venue = command.venue,
            watchType = command.watchType,
            cheeringTeam = command.cheeringTeam,
            result = command.result,
            title = command.title,
            content = command.content,
            seatInfo = command.seatInfo,
            companions = command.companions,
            mood = command.mood,
            cost = command.cost,
            weather = command.weather,
            isPublic = command.isPublic,
            images = command.images.map { WatchLogImage(imageUrl = it.imageUrl, imageType = it.imageType, sortOrder = it.sortOrder) },
            createdAt = now,
            updatedAt = now,
        )
        return watchLogRepository.save(watchLog)
    }

    override fun update(command: UpdateWatchLogCommand): WatchLog {
        val existing = findOwnedLog(command.watchLogId, command.requesterId)
        val updated = existing.copy(
            watchType = command.watchType,
            cheeringTeam = command.cheeringTeam,
            result = command.result,
            title = command.title,
            content = command.content,
            seatInfo = command.seatInfo,
            companions = command.companions,
            mood = command.mood,
            cost = command.cost,
            weather = command.weather,
            isPublic = command.isPublic,
            images = command.images.map { WatchLogImage(imageUrl = it.imageUrl, imageType = it.imageType, sortOrder = it.sortOrder) },
            updatedAt = LocalDateTime.now(),
        )
        return watchLogRepository.save(updated)
    }

    override fun delete(watchLogId: Long, requesterId: Long) {
        val watchLog = findOwnedLog(watchLogId, requesterId)
        watchLogRepository.delete(watchLog)
    }

    @Transactional(readOnly = true)
    override fun getById(watchLogId: Long, requesterId: Long?): WatchLog {
        val watchLog = watchLogRepository.findById(watchLogId)
            ?: throw WatchLogErrorCode.WATCH_LOG_NOT_FOUND.toException()
        if (!watchLog.isPublic && watchLog.memberId != requesterId) {
            throw WatchLogErrorCode.FORBIDDEN.toException()
        }
        return watchLog
    }

    @Transactional(readOnly = true)
    override fun getMyLogs(memberId: Long, pageable: Pageable): Page<WatchLog> =
        watchLogRepository.findByMemberId(memberId, pageable)

    @Transactional(readOnly = true)
    override fun getMyStats(memberId: Long, season: Int?): WatchStats {
        val currentYear = LocalDateTime.now().year
        val targetSeason = season ?: currentYear
        val seasonLogs = watchLogRepository.findAllByMemberIdAndSeason(memberId, targetSeason)
        val allLogs = watchLogRepository.findAllByMemberId(memberId)

        val stadiumLogs = allLogs.filter { it.watchType == WatchType.STADIUM }
        val homeLogs = allLogs.filter { it.watchType == WatchType.HOME }

        return WatchStats(
            totalCount = allLogs.size,
            stadiumCount = stadiumLogs.size,
            homeCount = homeLogs.size,
            winCount = allLogs.count { it.result == WatchResult.WIN },
            loseCount = allLogs.count { it.result == WatchResult.LOSE },
            drawCount = allLogs.count { it.result == WatchResult.DRAW },
            winRate = calculateWinRate(allLogs),
            stadiumWinRate = calculateWinRate(stadiumLogs),
            homeWinRate = calculateWinRate(homeLogs),
            currentStreak = calculateCurrentStreak(allLogs),
            bestWinStreak = calculateBestWinStreak(allLogs),
            visitedVenues = allLogs
                .filter { it.watchType == WatchType.STADIUM }
                .mapNotNull { it.venue }
                .distinct()
                .sorted(),
            seasonTotalCost = seasonLogs.sumOf { it.cost?.total ?: 0 },
        )
    }

    private fun calculateWinRate(logs: List<WatchLog>): Double {
        val rated = logs.filter { it.result.countsTowardRate }
        if (rated.isEmpty()) return 0.0
        return rated.count { it.result == WatchResult.WIN }.toDouble() / rated.size * 100
    }

    private fun calculateCurrentStreak(logs: List<WatchLog>): Streak {
        val rated = logs.filter { it.result.countsTowardRate }
            .sortedByDescending { it.createdAt }
        if (rated.isEmpty()) return Streak(StreakType.NONE, 0)

        val latestResult = rated.first().result
        val streakType = if (latestResult == WatchResult.WIN) StreakType.WIN else StreakType.LOSE
        val count = rated.takeWhile {
            (streakType == StreakType.WIN && it.result == WatchResult.WIN) ||
                (streakType == StreakType.LOSE && it.result == WatchResult.LOSE)
        }.size
        return Streak(streakType, count)
    }

    private fun calculateBestWinStreak(logs: List<WatchLog>): Int {
        val rated = logs.filter { it.result.countsTowardRate }
            .sortedBy { it.createdAt }
        var best = 0
        var current = 0
        for (log in rated) {
            if (log.result == WatchResult.WIN) {
                current++
                if (current > best) best = current
            } else {
                current = 0
            }
        }
        return best
    }

    private fun findOwnedLog(watchLogId: Long, requesterId: Long): WatchLog {
        val watchLog = watchLogRepository.findById(watchLogId)
            ?: throw WatchLogErrorCode.WATCH_LOG_NOT_FOUND.toException()
        if (watchLog.memberId != requesterId) throw WatchLogErrorCode.FORBIDDEN.toException()
        return watchLog
    }
}
