package com.log.watchlog.domain.model

data class WatchCost(
    val ticketCost: Int?,
    val foodCost: Int?,
    val transportCost: Int?,
) {
    val total: Int get() = (ticketCost ?: 0) + (foodCost ?: 0) + (transportCost ?: 0)
}
