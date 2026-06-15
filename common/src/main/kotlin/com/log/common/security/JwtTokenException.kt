package com.log.common.security

sealed class JwtTokenException(val code: String, message: String) : RuntimeException(message)

class InvalidTokenException : JwtTokenException("INVALID_TOKEN", "유효하지 않은 토큰입니다")
class ExpiredTokenException : JwtTokenException("EXPIRED_TOKEN", "만료된 토큰입니다")
