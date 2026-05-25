package com.log.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class RequestResponseLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request, MAX_BODY_LENGTH)
        val wrappedResponse = ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            logRequest(wrappedRequest)
            logResponse(wrappedResponse, System.currentTimeMillis() - startTime)
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        val queryString = request.queryString?.let { "?$it" } ?: ""
        val body = request.contentAsByteArray
            .toString(Charsets.UTF_8)
            .take(MAX_BODY_LENGTH)
            .let { if (it.isEmpty()) "(none)" else maskSensitiveFields(it) }

        log.info("REQUEST  {} {}{} body={}", request.method, request.requestURI, queryString, body)
    }

    private fun logResponse(response: ContentCachingResponseWrapper, elapsed: Long) {
        log.info("RESPONSE status={} elapsed={}ms", response.status, elapsed)
    }

    private fun maskSensitiveFields(body: String): String =
        MASK_PATTERN.replace(body) { "${it.groupValues[1]}****\"" }

    // actuator 등 불필요한 엔드포인트 제외
    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.requestURI.startsWith("/actuator")

    companion object {
        private const val MAX_BODY_LENGTH = 1_000

        private val SENSITIVE_KEYS = setOf(
            "password", "token", "accessToken", "refreshToken",
            "secret", "authorization", "cardNumber",
        )

        // JSON 문자열 값만 마스킹: "password": "secret123" -> "password": "****"
        private val MASK_PATTERN = Regex(
            "(\"(?:${SENSITIVE_KEYS.joinToString("|")})\"\\s*:\\s*\")([^\"]*)(\")",
            RegexOption.IGNORE_CASE,
        )
    }
}
