package endterm.service

import endterm.model.Dto.UserDto
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class TokenService {

    val username: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).username
        }

    val address: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).address
        }

    val education: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).education
        }

    val phone: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).phone
        }

    val birthDate: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).birthDate
        }

    val firstName: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).firstname
        }

    val lastName: String?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).lastname
        }

    val personId: Long?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return (authentication.principal as UserDto).personId
        }

    fun dataToMap(nationality: String): Map<String, String> {
        return mapOf(
            "fullname" to firstName!! + " " + lastName!!,
            "address" to address!!,
            "birthdate" to birthDate!!,
            "nationality" to nationality,
            "phonenumber" to phone!!,
            "email" to username!!,
            "education" to education!!
        )
    }

}