package endterm.controller

import endterm.model.Dto.AuthHttpMessage
import endterm.model.User
import endterm.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun loginPlatonus(@RequestBody user: User): AuthHttpMessage? {
        return user.login?.let { user.password?.let { it1 -> userService.getAuthenticated(it, it1) } }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getGrades")
    fun getGrades(): Any? {
        return userService.getGrades()
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getGroup/{groupId}")
    fun getGroup(@PathVariable groupId: Long): Any? {
        return userService.getGroup(groupId)
    }

}