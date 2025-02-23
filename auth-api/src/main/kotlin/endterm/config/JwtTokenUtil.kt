package endterm.config

import endterm.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
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

    fun doGenerateToken(user: User, token: String, cookie: String): String {
        return Jwts.builder()
            .claim("username", user.login)
            .claim("token", token)
            .claim("cookie", cookie)
            .claim("firstName", user.firstName)
            .claim("lastName", user.lastName)
            .claim("address", user.address)
            .claim("education", user.education)
            .claim("groupName", user.groupName)
            .claim("phoneNumber", user.phone)
            .claim("birthDate", user.birthDate)
            .claim("personId", user.personId)
            .claim("role", user.role)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtValidationTime!!))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact()
    }

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

    fun getTokenFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["token"].toString() }
    }

    fun getCookieFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> obj["cookie"].toString() }
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token, Function { obj: Claims -> obj.expiration })
    }

    fun getRoleFromToken(token: String?): String {
        return getClaimFromToken(token) { obj: Claims -> "ROLE_${obj["role"].toString()}" }
    }

}