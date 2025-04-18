package endterm.service

import endterm.config.JwtTokenUtil
import endterm.exception.CustomException
import endterm.model.Dto.AuthHttpMessage
import endterm.model.Dto.Filter
import endterm.model.Dto.PersonIdResponse
import endterm.model.Dto.UserInfoResponse
import endterm.model.Group
import endterm.model.User
import endterm.repository.GroupRepository
import endterm.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(
    @Value("\${platonus.api.url}") val platonusApiUrl: String,
    private val userRepository: UserRepository,
    private val restTemplateService: RestTemplateService,
    private val groupRepository: GroupRepository,
    private val jwtTokenUtil: JwtTokenUtil,
    private val tokenService: TokenService,
    private val groupService: GroupService
){

    val logger = LoggerFactory.getLogger(RestTemplateService::class.java)

    @Transactional(rollbackOn = [Exception::class])
    fun getAuthenticated(login: String, password: String): AuthHttpMessage {
        try{
            val authResponse = restTemplateService.authorize("$platonusApiUrl/rest/api/login", login, password)
            val personId = restTemplateService.sendPlatonus("$platonusApiUrl/rest/api/person/personID", authResponse.token!!, authResponse.cookie!!, PersonIdResponse::class.java)?.personID
            val response = restTemplateService.sendPlatonus("$platonusApiUrl/rest/student/studentInfo/$personId/ru", authResponse.token, authResponse.cookie, UserInfoResponse::class.java)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials")
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
                this.fullName = student.firstnameEN + " " + student.lastnameEN
                this.gpa = student.GPA
                this.phone = student.mobilePhone
                this.groupName = student.groupName
                this.specializationName = student.specializationNameEn
                this.password = password
                this.login = login
                this.courseNumber = student.courseNumber
                this.group = group
                this.address = student.adress
                this.education = student.education
                this.birthDate = student.birthDate
            }

            if (userRepository.findByPersonId(user.personId!!) == null) {
                user.role = "USER"
                userRepository.save(user)
                logger.info("User ${user.login} was saved")
                updateGroup(group)
            }
            user.rating = groupService.getStudentRating(login, group.id!!)
            userRepository.save(user)
            val jwt = jwtTokenUtil.doGenerateToken(user, authResponse.token, authResponse.cookie)

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

    fun updateGroup(group: Group) {
        val count = userRepository.countUsersByGroupId(group.id!!)
        val averageGpa = BigDecimal(userRepository.getAverageGpa(group.id!!)!!).setScale(3, RoundingMode.HALF_UP).toDouble()

        group.studentCount = count
        group.averageGpa = averageGpa
        groupRepository.save(group)
    }

    fun getAll(): List<User> {
        return userRepository.findAll()
    }

    fun getUsersByFilter(filter: Filter): ResponseEntity<Any> {
        try{
            val users = userRepository.getUsersFiltered(filter)
            val response = mapOf(
                "users" to users,
                "count" to users.count()
            )
            return ResponseEntity.ok(response)
        } catch (e: Exception){
            logger.error("Error in getUserFiltered: ", e)
            throw CustomException("Error in getUserFiltered")
        }
    }

    fun getUser(): User? {
        val user = userRepository.findByLogin(tokenService.username!!)
        if (user != null){
            return user
        } else {
            throw CustomException("No user found")
        }
    }

}