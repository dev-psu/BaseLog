package com.log.member.domain.model

@JvmInline
value class Nickname(val value: String) {
    init {
        require(value.length in 2..20) { "닉네임은 2자 이상 20자 이하여야 합니다." }
        require(value.matches(Regex("^[a-zA-Z0-9가-힣_]+$"))) { "닉네임은 영문, 숫자, 한글, 밑줄(_)만 사용할 수 있습니다." }
    }
}
