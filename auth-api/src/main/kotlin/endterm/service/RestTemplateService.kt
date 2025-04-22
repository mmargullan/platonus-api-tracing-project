package endterm.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import endterm.exception.CustomException
import endterm.model.Dto.AuthResponse
import endterm.model.Dto.LoginRequest
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTemplateService() {

    val logger = LoggerFactory.getLogger(RestTemplateService::class.java)

    fun authorize(url: String, login: String, password: String): AuthResponse {
        try {
            val restTemplate = RestTemplate()
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }

            val credentials = Gson().toJson(LoginRequest(login, password))
            val request = HttpEntity(credentials, headers)
            val response = restTemplate.exchange(url, HttpMethod.POST, request, String::class.java)
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



}