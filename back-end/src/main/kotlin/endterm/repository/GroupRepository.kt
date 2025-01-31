package endterm.repository

import endterm.model.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepository: JpaRepository<Group, Long> {

    override fun findById(groupId: Long): Optional<Group>

}