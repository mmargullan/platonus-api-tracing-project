package endterm.component

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
        val date = LocalDate.parse(p.text, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }
}

class DateSerializer : JsonSerializer<String>() {
    override fun serialize(value: String?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value != null) {
            val date = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            gen.writeString(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        } else {
            gen.writeNull()
        }
    }
}