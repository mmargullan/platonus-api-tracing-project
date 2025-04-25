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
        filter.groupName?.let {
            sql.append(" AND group_name LIKE :groupName")
            params["groupName"] = "%$it%"
        }
        val name = filter.name?.trim()
        if (name != null && name.contains(" ")) {
            val parts = name.split("\\s+".toRegex(), 2)
            val first = parts[0]
            val last = parts[1]
            sql.append(" AND (full_name LIKE :name1 OR full_name LIKE :name2)")
            params["name1"] = "%$first $last%"
            params["name2"] = "%$last $first%"
        } else if (name != null) {
            sql.append(" AND (first_name LIKE :name OR last_name LIKE :name)")
            params["name"] = "%$name%"
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