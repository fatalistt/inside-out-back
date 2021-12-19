package ru.itmo.mpi.api.plugins

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
        }
    }
}
