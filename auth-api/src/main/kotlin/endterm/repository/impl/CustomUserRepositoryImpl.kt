package endterm.repository.impl

import endterm.model.Dto.Filter
import endterm.model.User
import endterm.repository.CustomUserRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query

class CustomUserRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager
): CustomUserRepository {

    override fun getUsersFiltered(filter: Filter): List<User> {
        val query = getQuery(filter, false)
        return query.resultList as List<User>
    }

    fun getQuery(filter: Filter, isCount: Boolean): Query {
        val sql = StringBuilder()

        if (isCount) {
            sql.append("SELECT COUNT(*) FROM users WHERE 1=1")
        } else {
            sql.append("SELECT * FROM users WHERE 1=1")
        }

        val params = mutableMapOf<String, Any>()

        filter.groupId?.let {
            sql.append(" AND group_id = :groupId")
            params["groupId"] = it
        }
        filter.groupName?.let {
            sql.append(" AND group_name LIKE :groupName")
            params["groupName"] = "%$it%"
        }
        val bool = filter.name?.trim()?.contains(" ")
        filter.name?.let {
            sql.append(" AND first_name LIKE :name")
            sql.append(" AND last_name LIKE :name")
            params["name"] = "%$it%"
        }
        filter.courseNumber?.let {
            sql.append(" AND course_number = :courseNumber")
            params["courseNumber"] = it
        }

        if (!isCount) {
            sql.append(" ORDER BY id DESC")

            filter.limit?.let {
                sql.append(" LIMIT :limit")
                params["limit"] = it
            }
            filter.offset?.let {
                sql.append(" OFFSET :offset")
                params["offset"] = it
            }
        }

        val query: Query = entityManager.createNativeQuery(sql.toString(), if (!isCount) User::class.java else null)
        params.forEach { (key, value) -> query.setParameter(key, value) }

        return query
    }

}