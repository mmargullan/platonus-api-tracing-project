package endterm.controller

import endterm.model.Dto.AuthHttpMessage
import endterm.model.Dto.Filter
import endterm.model.User
import endterm.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun loginPlatonus(@RequestBody(required = true) user: User): AuthHttpMessage? {
        return userService.getAuthenticated(user.login, user.password)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    fun getAll(): List<User> {
        return userService.getAll()
    }

    @GetMapping("/getUsersFiltered")
    fun getUsersFiltered(@RequestBody filter: Filter): ResponseEntity<Any> {
        return userService.getUsersFiltered(filter)
    }

    @GetMapping("/getUser")
    fun getUser(): User? {
        return userService.getUser()
    }

}