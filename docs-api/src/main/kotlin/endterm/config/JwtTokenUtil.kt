package endterm.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*
import java.util.function.Function

@Component
class JwtTokenUtil: Serializable {

    @Value("\${jwt.secret}")
    private val jwtSecret: String? = null

    @Value("\${jwt.validation.time}")
    private val jwtValidationTime: Int? = null

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(jwtSecret!!)
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    fun getUsernameFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["username"].toString() }
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token, Function { obj: Claims -> obj.expiration })
    }

    fun getRoleFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["role"].toString() }
    }

    fun getEducationFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["education"].toString() }
    }

    fun getAddressFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["address"].toString() }
    }

    fun getPhoneFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["phoneNumber"].toString() }
    }

    fun getBirthDateFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["birthDate"].toString() }
    }

    fun getFirstNameFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["firstName"].toString() }
    }

    fun getLastNameFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["lastName"].toString() }
    }

    fun getPersonIdFromToken(token: String?): Long {
        return getClaimFromToken(token) { obj: Claims -> obj["personId"].toString() }.toLong()
    }

}