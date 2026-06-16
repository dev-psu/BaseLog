package com.log.member.domain.exception

import com.log.common.exception.BusinessException
import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val message: String) {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다"),
    SOCIAL_AUTH_FAILED(HttpStatus.BAD_GATEWAY, "소셜 로그인 처리에 실패했습니다"),
    INVALID_ONBOARDING_TOKEN(HttpStatus.UNAUTHORIZED, "온보딩 토큰이 유효하지 않거나 만료되었습니다"),
    ;

    fun toException(): BusinessException = BusinessException(httpStatus, name, message)
}
