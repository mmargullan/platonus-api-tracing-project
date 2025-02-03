package endterm.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@RestControllerAdvice
class ExceptionHandler {

    val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleException(ex: CustomException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message ?: "Unexpected error",
            timestamp = LocalDateTime.now().toString(),
            path = (request as ServletWebRequest).request.requestURL.toString()
        )
        logger.error("Unexpected error occured: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

}