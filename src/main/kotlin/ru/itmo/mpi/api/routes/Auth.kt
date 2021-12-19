package ru.itmo.mpi.api.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.koin.ktor.ext.inject
import ru.itmo.mpi.api.plugins.AuthenticationException
import ru.itmo.mpi.api.plugins.InsideOutSession
import ru.itmo.mpi.auth.login_password.LoginPasswordAuthProvider
import ru.itmo.mpi.dal.UserRepository

data class LoginResponse(val resultCode: Int, val message: Collection<String>)

data class LogoutResponse(val resultCode: Int)

data class UserInfo(val id: Int, val message: Collection<String>, val role: String)
data class InfoResponse(val resultCode: Int, val message: Collection<String>, val data: UserInfo)

fun Application.authRoute() {
    val loginProvider: LoginPasswordAuthProvider by inject()
    val userRepository: UserRepository by inject()

    routing {
        route("/auth") {
            route("/login") {
                post {
                    val user = loginProvider.tryAuthenticateUser(call) ?: throw AuthenticationException()

                    val session = InsideOutSession(user.id, user.roles)
                    call.sessions.set(session)

                    val response = LoginResponse(0, listOf("role"))
                    call.respond(response)
                }
                delete {
                    call.sessions.clear<InsideOutSession>()

                    val response = LogoutResponse(0)
                    call.respond(response)
                }
            }
            get("/me") {
                val session = call.sessions.get<InsideOutSession>() ?: throw AuthenticationException()
                val user = userRepository.getUser(session.id)
                if (user == null) {
                    call.sessions.clear<InsideOutSession>()
                    throw AuthenticationException()
                }
                val response = InfoResponse(0, emptyList(), UserInfo(user.id, emptyList(), user.roles.first()))
                call.respond(response)
            }
        }
    }
}
