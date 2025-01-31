package endterm.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import endterm.model.Dto.UserDto
import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthorizationFilter(
    private val jwtTokenUtil: JwtTokenUtil,
    @Value("\${jwt.secret}") private val jwtSecret: String
): OncePerRequestFilter() {

    private val LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val requestTokenHeader =request.getHeader("Authorization")
        val path = request.requestURI
        response.contentType = "application/json; charset=utf-8"
        var username: String? =null
        var jwtToken: String? =null
        val errorResponse = mutableMapOf<String, Any>()
        errorResponse["status"] = "error"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7)
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken)
            } catch (e: Exception) {
                errorResponse["message"] = "Invalid token"
                errorResponse["timestamp"] = LocalTime.now().toString()
                errorResponse["path"] = path
                val errorJson = Gson().toJson(errorResponse)
                response.status = HttpStatus.FORBIDDEN.value()
                response.writer.use { it.print(errorJson) }
                return
            }
        } else if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")){
            errorResponse["message"] = "No authorization header found or incorrect authorization header"
            errorResponse["timestamp"] = LocalDateTime.now().toString()
            errorResponse["path"] = path
            val errorJson = Gson().toJson(errorResponse)
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.use { it.print(errorJson) }
            return
        } else{
            LOGGER.warn("JWT Token does not begin with Bearer String or this request doesn't need Token")
        }
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtTokenUtil.validateToken(jwtToken!!)) {
                val userDto = UserDto().apply {
                    this.username = username
                    this.token = jwtTokenUtil.getTokenFromToken(jwtToken)
                    this.cookie = jwtTokenUtil.getCookieFromToken(jwtToken)
                }
                val authToken = UsernamePasswordAuthenticationToken(userDto, null, emptyList())
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)

    }

}