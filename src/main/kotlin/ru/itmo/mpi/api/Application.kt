package ru.itmo.mpi.api

import io.ktor.application.Application
import ru.itmo.mpi.api.plugins.configureHTTP
import ru.itmo.mpi.api.plugins.configureKoin
import ru.itmo.mpi.api.plugins.configureSecurity
import ru.itmo.mpi.api.plugins.configureSerialization
import ru.itmo.mpi.api.plugins.configureStatusPages
import ru.itmo.mpi.api.routes.configureRouting

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureRouting()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureKoin()
}
