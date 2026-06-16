package com.log.game.domain.exception

import com.log.common.exception.BusinessException
import org.springframework.http.HttpStatus

enum class GameErrorCode(val httpStatus: HttpStatus, val message: String) {
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다"),
    ;

    fun toException(): BusinessException = BusinessException(httpStatus, name, message)
}
