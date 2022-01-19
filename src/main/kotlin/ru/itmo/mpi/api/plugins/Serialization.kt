package ru.itmo.mpi.api.plugins

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import java.lang.reflect.Type
import java.time.Instant
import java.time.OffsetDateTime

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            this.registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeDeserializer)
            this.registerTypeAdapter(Instant::class.java, InstantSerializer)
        }
    }
}

private object OffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): OffsetDateTime =
        OffsetDateTime.parse(json?.asString)
}

private object InstantSerializer : JsonSerializer<Instant> {
    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement =
        if (src === null) {
            JsonNull.INSTANCE
        } else {
            JsonPrimitive(src.toString())
        }
}