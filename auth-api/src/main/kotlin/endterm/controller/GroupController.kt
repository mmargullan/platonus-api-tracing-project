package endterm.controller

import endterm.model.Group
import endterm.model.User
import endterm.service.GroupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/group")
class GroupController(
    private val groupService: GroupService,
) {

    @GetMapping("/getGroupById/{groupId}")
    fun getGroup(@PathVariable groupId: Long): Group {
        return groupService.getById(groupId)
    }

    @GetMapping("/getUsersByGroupId/{groupId}")
    fun getUsersByGroupId(@PathVariable groupId: Long): List<User> {
        return groupService.getAllStudents(groupId)
    }

}