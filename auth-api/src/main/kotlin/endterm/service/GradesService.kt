package endterm.service

import endterm.model.Dto.UserDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class GradesService(
    @Value("\${platonus.api.url}") val platonusApiUrl: String,
    private val restTemplateService: RestTemplateService
) {

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

    fun getGradesByUserId(year: Int, semester: Int): Any? {
        return restTemplateService.sendPlatonus("$platonusApiUrl/journal/$year/$semester", token!!, cookie!!, Any::class.java)
    }

}