package ro.pub.cs.systems.eim.practicaltest02v1

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class SuggestionResponseDeserializer : JsonDeserializer<SuggestionResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SuggestionResponse {
        val jsonArray = json.asJsonArray
        val query = jsonArray[0].asString
        val suggestions = jsonArray[1].asJsonArray.map { it.asString }
        return SuggestionResponse(query, suggestions)
    }
}