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

    fun getById(id: Long): Group {
        val group = groupRepository.findById(id)
        return if (group.isPresent) group.get() else throw CustomException("Group not found")
    }

    fun getAllStudents(groupId: Long): List<User> {
        return userRepository.findUsersByGroupId(groupId)
    }

    fun getStudentRating(gpa: Double, groupId: Long): Int {
        val students = getAllStudents(groupId)
//        for (i in students.indices) {
//            if (students[i].login == username) {
//                return i + 1
//            }
//        }
        var left = 0
        var right = students.size - 1
        while (left <= right) {
            val middle = (left + right) / 2
            if (students[middle].gpa == gpa) {
                return middle + 1
            } else if (students[middle].gpa!! < gpa) {
                right = middle - 1
            } else {
                left = middle + 1
            }
        }
        return left + 1
    }

    fun updateStudentsRating(students: List<User>) {
        for (student in students) {
            student.rating = getStudentRating(student.gpa!!, student.group?.id!!)
        }
        userRepository.saveAll(students)
    }

}