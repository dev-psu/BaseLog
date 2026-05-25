package com.log.common.exception

import com.log.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.http.converter.HttpMessageNotReadableException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(ex: BusinessException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(ex.httpStatus).body(ApiResponse.fail(ex.code, ex.message!!))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("INVALID_INPUT", message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("INVALID_INPUT", "요청 본문을 읽을 수 없습니다"))

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("MISSING_PARAMETER", "${ex.parameterName} 파라미터가 필요합니다"))

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("INVALID_TYPE", "${ex.name}의 타입이 올바르지 않습니다"))

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ApiResponse.fail("METHOD_NOT_ALLOWED", "${ex.method} 메서드는 지원하지 않습니다"))

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandler(ex: NoHandlerFoundException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.fail("NOT_FOUND", "${ex.requestURL} 경로를 찾을 수 없습니다"))

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Unhandled exception", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다"))
    }
}
