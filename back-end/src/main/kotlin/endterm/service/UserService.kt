package endterm.service

import com.google.gson.Gson
import endterm.config.JwtTokenUtil
import endterm.model.Dto.HttpMessage
import endterm.model.Dto.PersonResponse
import endterm.model.Dto.UserDto
import endterm.model.User
import endterm.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(
    @Value("\${platonus.login.url}") val loginUrl: String,
    @Value("\${platonus.personID.url}") val personIDurl: String,
    @Value("\${platonus.grades.url}") val gradesUrl: String,
    @Value("\${platonus.userInfo.url}") val userInfoUrl: String,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val restTemplateService: RestTemplateService,
    private val jwtTokenUtil: JwtTokenUtil
){
    val logger = LoggerFactory.getLogger(RestTemplateService::class.java)

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

    fun getAuthenticated(login: String, password: String): Any? {

        val authResponse = restTemplateService.authorize(login, password)
        val response = restTemplateService.sendPlatonus(personIDurl, authResponse.token!!, authResponse.cookie!!, PersonResponse::class.java)
        logger.info("Response: ${Gson().toJson(response)}")

        val user = User().apply {
            this.personId = response?.personID
            this.password = password
            this.login = login
        }
        if (userRepository.findByLogin(login) == null) {
            userRepository.save(user)
        }
        val jwt = jwtTokenUtil.doGenerateToken(login, authResponse.token, authResponse.cookie)
        return HttpMessage().apply {
            this.status = "ok"
            this.message = "Successfully logged in"
            this.token = jwt
        }
    }

    fun getGrades(): Any? {
        val response = restTemplateService.sendPlatonus(gradesUrl, token!!, cookie!!, Any::class.java)
        return response
    }

    fun getUserInfo(): Any? {
        val response = restTemplateService.sendPlatonus(userInfoUrl, token!!, cookie!!, Any::class.java)
        return response
    }

}

