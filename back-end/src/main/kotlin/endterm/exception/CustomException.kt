package endterm.exception

class CustomException(message: String) : Exception(message) {

    companion object {
        const val INTERNAL_ERROR = "Internal error"
    }

}