package com.log.watchlog.domain.exception

import com.log.common.exception.BusinessException
import org.springframework.http.HttpStatus

enum class WatchLogErrorCode(
    private val httpStatus: HttpStatus,
    private val message: String,
) {
    WATCH_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "기록을 찾을 수 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 기록에 대한 권한이 없습니다");

    fun toException() = BusinessException(httpStatus, name, message)
}
