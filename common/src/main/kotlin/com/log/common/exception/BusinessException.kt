package com.log.common.exception

open class BusinessException(
    val code: String,
    override val message: String,
) : RuntimeException(message)
