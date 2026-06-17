package com.log.watchlog.domain.model

data class WatchStats(
    val totalCount: Int,
    val stadiumCount: Int,
    val homeCount: Int,
    val winCount: Int,
    val loseCount: Int,
    val drawCount: Int,
    val winRate: Double,
    val stadiumWinRate: Double,
    val homeWinRate: Double,
    val currentStreak: Streak,
    val bestWinStreak: Int,
    val visitedVenues: List<String>,
    val seasonTotalCost: Int,
)

data class Streak(
    val type: StreakType,
    val count: Int,
)

enum class StreakType { WIN, LOSE, NONE }
