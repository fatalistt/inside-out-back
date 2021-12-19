package ru.itmo.mpi.auth

import io.ktor.application.ApplicationCall
import ru.itmo.mpi.core.User

abstract class AuthProviderBase {
    abstract suspend fun tryAuthenticateUser(call: ApplicationCall): User?
}