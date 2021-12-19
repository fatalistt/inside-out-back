package ru.itmo.mpi.auth.login_password

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import ru.itmo.mpi.auth.AuthProviderBase
import ru.itmo.mpi.core.User
import ru.itmo.mpi.dal.LoginPasswordRepository
import ru.itmo.mpi.dal.UserRepository

data class LoginRequest(val login: String, val password: String)

class LoginPasswordAuthProvider(
    private val loginRepository: LoginPasswordRepository,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) : AuthProviderBase() {
    override suspend fun tryAuthenticateUser(call: ApplicationCall): User? {
        val request = call.receive<LoginRequest>()
        val (userId, passwordHash) = loginRepository.getUserIdPassword(request.login) ?: return null
        if (!passwordHasher.isPasswordValid(request.password, passwordHash)) {
            return null
        }
        return userRepository.getUser(userId)!!
    }
}