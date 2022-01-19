package ru.itmo.mpi.core

import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

fun String.toInstant(): Instant {
    return OffsetDateTime.parse(this).toInstant()
}

fun String.toInstantOrNull(): Instant? = try {
    toInstant()
} catch (e: DateTimeParseException) {
    null
}
