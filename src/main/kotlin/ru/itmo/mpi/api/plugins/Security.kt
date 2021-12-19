package ru.itmo.mpi.api.plugins

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import kotlin.collections.set

data class InsideOutSession(val id: Int, val roles: Collection<String>)

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<InsideOutSession>("INSIDE_OUT_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}
