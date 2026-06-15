package com.log.common.security

import tools.jackson.databind.ObjectMapper
import com.log.common.response.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractBearerToken(request)

        if (token != null) {
            try {
                val memberId = jwtProvider.validateAndGetMemberId(token)
                val auth = UsernamePasswordAuthenticationToken(memberId, null, emptyList())
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: JwtTokenException) {
                SecurityContextHolder.clearContext()
                writeUnauthorizedResponse(response, e.code, e.message!!)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractBearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        if (!header.startsWith("Bearer ")) return null
        return header.substring(BEARER_PREFIX_LENGTH)
    }

    private fun writeUnauthorizedResponse(response: HttpServletResponse, code: String, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write(objectMapper.writeValueAsString(ApiResponse.fail<Nothing>(code, message)))
    }

    companion object {
        private const val BEARER_PREFIX_LENGTH = 7
    }
}
