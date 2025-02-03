package endterm.service

import com.google.gson.Gson
import endterm.config.JwtTokenUtil
import endterm.exception.CustomException
import endterm.model.Dto.AuthHttpMessage
import endterm.model.Dto.PersonIdResponse
import endterm.model.Dto.UserDto
import endterm.model.Dto.UserInfoResponse
import endterm.model.Group
import endterm.model.User
import endterm.repository.GroupRepository
import endterm.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(
    @Value("\${platonus.login.url}") val loginUrl: String,
    @Value("\${platonus.personID.url}") val personIDurl: String,
    @Value("\${platonus.grades.url}") val gradesUrl: String,
    @Value("\${platonus.userInfo.url}") val userInfoUrl: String,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val restTemplateService: RestTemplateService,
    @Autowired private val groupRepository: GroupRepository,
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

    @Transactional(rollbackOn = [Exception::class])
    fun getAuthenticated(login: String, password: String): AuthHttpMessage {

        try{
            val authResponse = restTemplateService.authorize(loginUrl, login, password)
            val personId = restTemplateService.sendPlatonus(personIDurl, authResponse.token!!, authResponse.cookie!!, PersonIdResponse::class.java)?.personID
            val response = restTemplateService.sendPlatonus(userInfoUrl + "/$personId/ru", authResponse.token!!, authResponse.cookie!!, UserInfoResponse::class.java) ?:
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials")
            val student = response.student ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request")

            val group = Group().apply {
                this.id = student.groupID
                this.name = student.groupName
            }
            if (!groupRepository.findById(group.id!!).isPresent){
                groupRepository.save(group)
                logger.info("Group ${group.name} was saved")
            }

            val user = User().apply {
                this.personId = student.personID
                this.firstName = student.firstnameEN
                this.lastName = student.lastnameEN
                this.gpa = student.GPA
                this.phone = student.mobilePhone
                this.groupName = student.groupName
                this.specializationName = student.specializationNameEn
                this.password = password
                this.login = login
                this.courseNumber = student.courseNumber
                this.group = group
            }
            if (userRepository.findByPersonId(user.personId!!) == null) {
                userRepository.save(user)
                logger.info("User ${user.login} was saved")
                updateGroup(group)
            }

            val jwt = jwtTokenUtil.doGenerateToken(login, authResponse.token, authResponse.cookie)

            return AuthHttpMessage().apply {
                this.status = "ok"
                this.message = "Successfully logged in"
                this.token = jwt
            }

        }catch (ex: Exception){
            logger.error("Error while logging in: ", ex)
            throw CustomException(CustomException.BAD_REQUEST)
        }
    }

    fun getGrades(): Any? {
        val response = restTemplateService.sendPlatonus(userInfoUrl, token!!, cookie!!, Any::class.java)
        return response
    }

    fun updateGroup(group: Group) {
        val count = userRepository.countUsersByGroupId(group.id!!)
        val averageGpa = userRepository.getAverageGpa(group.id!!)

        group.studentCount = count
        group.averageGpa = averageGpa
        groupRepository.save(group)
    }

    fun getGroup(groupId: Long): Optional<Group> {
        return groupRepository.findById(groupId)
    }

}

