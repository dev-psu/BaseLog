package com.log.member.domain.exception

import com.log.common.exception.BusinessException
import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val message: String) {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    ;

    fun toException(): BusinessException = BusinessException(httpStatus, name, message)
}
