package endterm.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import endterm.exception.CustomException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

@Component
class RestTemplateService(
    @Value("\${platonus.login.url}") val loginUrl: String,
) {

    val logger = LoggerFactory.getLogger(RestTemplateService::class.java)

    fun authorize(login: String, password: String): AuthResponse {
        try {
            val restTemplate = RestTemplate()
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }

            val creds = LoginRequest(login, password)
            val gson = Gson()
            val credentials = gson.toJson(creds)
            val request = HttpEntity(credentials, headers)

            val response = restTemplate.exchange(loginUrl, HttpMethod.POST, request, String::class.java)
            val jsonResponse = Gson().fromJson(response.body, JsonObject::class.java)

            val cookie = response.headers[HttpHeaders.SET_COOKIE]?.first()
            val token = jsonResponse["auth_token"]?.asString

            return AuthResponse(token, cookie)

        }catch (e: Exception) {
            logger.error(e.message, e)
            throw CustomException(e.message.toString())
        }
    }

    fun <T> sendPlatonus(url: String, token: String?, cookie: String?, classType: Class<T>): T? {
        try {
            val restTemplate = RestTemplate()

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Token", token)
                set("Cookie", cookie)
            }

            val request = HttpEntity<Any>("{}", headers)
            val response = restTemplate.exchange(url, HttpMethod.GET, request, classType)
            return response.body
        }catch (e: Exception){
            logger.error(e.message, e)
            throw CustomException(e.message.toString())
        }
    }

    data class AuthResponse(
        val token: String?,
        val cookie: String?
    )

    data class LoginRequest(val login: String, val password: String)

}