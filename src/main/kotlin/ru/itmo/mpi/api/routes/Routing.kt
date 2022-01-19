package ru.itmo.mpi.api.routes

import io.ktor.application.Application

fun Application.configureRouting() {
    authRoute()
    memoryRoute()
}
