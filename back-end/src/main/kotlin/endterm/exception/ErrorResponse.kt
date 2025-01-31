package endterm.exception

import java.time.LocalDateTime
import java.util.*

data class ErrorResponse(
    val status: Int,
    val message: String,
    val path: String = "",
    val timestamp: String = LocalDateTime.now().toString()
)