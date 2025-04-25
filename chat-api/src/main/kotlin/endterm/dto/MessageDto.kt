package endterm.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ChatMessage @JsonCreator constructor(
    @JsonProperty("from") val from: String ?= null,
    @JsonProperty("text") val text: String ?= null,
    @JsonProperty("date") val date: String ?= null
)