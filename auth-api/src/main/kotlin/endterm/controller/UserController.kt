package endterm.controller

import endterm.model.Dto.AuthHttpMessage
import endterm.model.Dto.Filter
import endterm.model.User
import endterm.repository.UserRepository
import endterm.service.GroupService
import endterm.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val userRepository: UserRepository
) {

    @PostMapping("/login")
    fun loginPlatonus(@RequestBody(required = true) user: User): AuthHttpMessage? {
        return userService.getAuthenticated(user.login, user.password)
    }

    @GetMapping("/getAll")
    fun getAll(): List<User> {
        return userRepository.findAll()
    }

    @PostMapping("/getUsersByFilter")
    fun getUsersByFilter(@RequestBody filter: Filter): ResponseEntity<Any> {
        return userService.getUsersByFilter(filter)
    }

    @GetMapping("/getUser")
    fun getUser(): User? {
        return userService.getUser()
    }

}