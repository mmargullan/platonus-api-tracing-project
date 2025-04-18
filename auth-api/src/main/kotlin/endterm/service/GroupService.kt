package endterm.service

import endterm.exception.CustomException
import endterm.model.Group
import endterm.model.User
import endterm.repository.GroupRepository
import endterm.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GroupService(
    val groupRepository: GroupRepository,
    val userRepository: UserRepository,
    private val tokenService: TokenService
) {

    fun getAllGroups(): List<Group> {
        return groupRepository.findAll()
    }

    fun getById(id: Long): Group {
        val group = groupRepository.findById(id)
        return if (group.isPresent) group.get() else throw CustomException("Group not found")
    }

    fun getAllStudents(groupId: Long): List<User> {
        return userRepository.findUsersByGroupId(groupId)
    }

    fun getStudentRating(username: String, groupId: Long): Int {
        val groupExists = groupRepository.findById(groupId)
        val group: Group
        if (groupExists.isPresent) {
            group = groupExists.get()
        } else{
            throw CustomException("Group not found")
        }
        val students = getAllStudents(groupId)
        for (i in students.indices) {
            if (students[i].login == username) {
                return i + 1
            }
        }
        return 0
    }

}