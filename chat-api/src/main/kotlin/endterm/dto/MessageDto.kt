package endterm.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ChatMessage @JsonCreator constructor(
    @JsonProperty("from") val from: String,
    @JsonProperty("text") val text: String,
    @JsonProperty("date") val date: String,
)