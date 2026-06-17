package com.log.watchlog.domain.model

enum class WatchResult {
    WIN,
    LOSE,
    DRAW,
    SUSPENDED,
    CANCELED;

    val countsTowardRate: Boolean get() = this == WIN || this == LOSE || this == DRAW
}
