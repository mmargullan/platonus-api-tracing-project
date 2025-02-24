package endterm.config

import com.google.gson.Gson
import endterm.model.Dto.UserDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime
import java.time.LocalTime
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

        if (isPermittedPath(path)) {
            filterChain.doFilter(request, response)
            return
        }

        LOGGER.info("Filtration in process")
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
                val address = jwtTokenUtil.getAddressFromToken(jwtToken)
                val education = jwtTokenUtil.getEducationFromToken(jwtToken)
                val phone = jwtTokenUtil.getPhoneFromToken(jwtToken)
                val birthDate = jwtTokenUtil.getBirthDateFromToken(jwtToken)
                val firstName = jwtTokenUtil.getFirstNameFromToken(jwtToken)
                val lastName = jwtTokenUtil.getLastNameFromToken(jwtToken)
                val personId = jwtTokenUtil.getPersonIdFromToken(jwtToken)
                val userDto = UserDto().apply {
                    this.username = username
                    this.address = address
                    this.education = education
                    this.firstname = firstName
                    this.lastname = lastName
                    this.phone = phone
                    this.birthDate = birthDate
                    this.personId = personId
                    this.role = listOf(jwtTokenUtil.getRoleFromToken(jwtToken))
                }
                val authorities = userDto.role?.map { SimpleGrantedAuthority(it) }

                val authToken = UsernamePasswordAuthenticationToken(userDto, null, authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun isPermittedPath(path: String): Boolean {
        val permittedPaths = listOf(
            "/user/login",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/swagger-resources",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/webjars/"
        )
        return permittedPaths.any { path.startsWith(it) }
    }

}