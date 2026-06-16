package com.log.member.domain.model

@JvmInline
value class Email(val value: String) {
    init {
           require(value.matches(Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))) {
           "유효하지 않은 이메일 형식입니다: $value"
        }
    }
}
