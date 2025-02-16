package endterm.controller

import endterm.model.Dto.AuthHttpMessage
import endterm.model.User
import endterm.service.UserService
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

    @GetMapping("/getGrades")
    fun getGrades(): Any? {
        return userService.getGrades()
    }

    @GetMapping("/getGroup/{groupId}")
    fun getGroup(@PathVariable groupId: Long): Any? {
        return userService.getGroup(groupId)
    }

}