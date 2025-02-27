package endterm.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GradesService(
    @Value("\${platonus.api.url}") val platonusApiUrl: String,
    private val restTemplateService: RestTemplateService,
    private val tokenService: TokenService
) {

    fun getGradesByUserId(year: Int, semester: Int): Any? {
        return restTemplateService.sendPlatonus("$platonusApiUrl/journal/$year/$semester", tokenService.token, tokenService.cookie, Any::class.java)
    }

}