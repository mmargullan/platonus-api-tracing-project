package endterm.service

import endterm.config.JwtTokenUtil
import endterm.exception.CustomException
import endterm.model.Dto.*
import endterm.model.Group
import endterm.model.User
import endterm.repository.GroupRepository
import endterm.repository.UserRepository
import org.slf4j.LoggerFactory
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

    val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(rollbackOn = [Exception::class])
    fun getAuthenticated(login: String, password: String): AuthHttpMessage {
        try {
            val authResponse = restTemplateService.authorize("$platonusApiUrl/rest/api/login", login, password)
            val personId = restTemplateService.sendPlatonus("$platonusApiUrl/rest/api/person/personID", authResponse.token!!, authResponse.cookie!!, PersonIdResponse::class.java)?.personID
            val response = restTemplateService.sendPlatonus("$platonusApiUrl/rest/student/studentInfo/$personId/ru", authResponse.token, authResponse.cookie, UserInfoResponse::class.java)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials")
            val student = response.student ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request")

            val group = Group()
            group.id = student.groupID
            group.name = student.groupName

            val groupExists = groupRepository.findById(group.id!!)
            if (!groupExists.isPresent) {
                groupRepository.save(group)
                logger.info("Group ${group.name} was saved")
            } else {
                val groupSave = groupExists.get()
                groupRepository.save(groupSave)
            }

            val jwt: String
            val user = User()
            val findUser = userRepository.findByPersonId(student.personID!!)
            if (findUser == null) {
                user.role = "USER"
                user.login = login
                user.password = password
                val userSave = studentToUser(student, user, group)
                userRepository.save(userSave)
                logger.info("User ${userSave.login} was saved")
                updateGroup(group)
                jwt = jwtTokenUtil.doGenerateToken(userSave, authResponse.token, authResponse.cookie)
            } else {
                val userSave = studentToUser(student, findUser, group)
                userRepository.save(userSave)
                jwt = jwtTokenUtil.doGenerateToken(userSave, authResponse.token, authResponse.cookie)
            }

            return AuthHttpMessage(
                status = "ok",
                message = "Successfully logged in",
                token = jwt
            )
        } catch (ex: Exception) {
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

    fun getUsersByFilter(filter: Filter): ResponseEntity<Any> {
        try {
            val users = userRepository.getUsersFiltered(filter)
            val response = mapOf(
                "users" to users,
                "count" to users.count()
            )
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Error in getUserFiltered: ", e)
            throw CustomException("Error in getUserFiltered")
        }
    }

    fun getUser(): User? {
        val user = userRepository.findByLogin(tokenService.username!!)
        if (user != null) {
            return user
        } else {
            throw CustomException("No user found")
        }
    }

    fun studentToUser(student: Student, user: User, group: Group): User {
        user.personId = student.personID
        user.firstName = student.firstnameEN
        user.lastName = student.lastnameEN
        user.fullName = student.firstnameEN + " " + student.lastnameEN
        user.gpa = student.GPA
        user.phone = student.mobilePhone
        user.groupName = student.groupName
        user.specializationName = student.specializationNameEn
        user.courseNumber = student.courseNumber
        user.group = group
        user.address = student.adress
        user.education = student.education
        user.birthDate = student.birthDate
        user.rating = groupService.getStudentRating(user.login, group.id!!)
        return user
    }

}