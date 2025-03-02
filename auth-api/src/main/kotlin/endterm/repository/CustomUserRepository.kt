package endterm.repository

import endterm.model.Dto.Filter
import endterm.model.User

interface CustomUserRepository {

    fun getUsersFiltered(filter: Filter): List<User>

}