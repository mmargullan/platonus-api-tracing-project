package endterm.model.Dto

data class AuthResponse(
    val token: String?,
    val cookie: String?
)

data class LoginRequest(
    val login: String,
    val password: String
)