package endterm.service

import endterm.model.Dto.UserDto
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class TokenService {

    val username: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).username
        }

    val token: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).token
        }

    val cookie: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).cookie
        }

}