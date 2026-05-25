package com.log.common.exception

import org.springframework.http.HttpStatus

open class BusinessException(
    val httpStatus: HttpStatus,
    val code: String,
    override val message: String,
) : RuntimeException(message)
