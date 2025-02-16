package endterm.repository

import endterm.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByPersonId(personId: Long): User?
    fun countUsersByGroupId(groupId: Long): Long

    @Query("SELECT COALESCE(AVG(u.gpa), 0.0) FROM User u WHERE u.group.id = :groupId")
    fun getAverageGpa(groupId: Long): Double?
    fun findByLogin(login: String): User?

}