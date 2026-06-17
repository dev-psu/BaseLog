package com.log.watchlog.domain.port.input

interface DeleteWatchLogUseCase {
    fun delete(watchLogId: Long, requesterId: Long)
}
