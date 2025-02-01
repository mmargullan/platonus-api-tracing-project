package endterm.exception

class CustomException(message: String) : Exception(message) {

    companion object {
        const val BAD_REQUEST = "Bad request"
    }

}