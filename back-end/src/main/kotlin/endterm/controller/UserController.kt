package endterm.controller

import endterm.model.User
import endterm.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun loginPlatonus(@RequestBody user: User): Any? {
        return user.login?.let { user.password?.let { it1 -> userService.getAuthenticated(it, it1) } }
    }

    @GetMapping("/getGrades")
    fun getGrades(): Any? {
        return userService.getGrades()
    }

    @GetMapping("/getInfo")
    fun getUserInfo(): Any? {
        return userService.getUserInfo()
    }

}