package ru.itmo.mpi.api.plugins

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.DefaultHeaders

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
}
