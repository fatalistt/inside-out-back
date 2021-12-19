package ru.itmo.mpi.auth.login_password

interface PasswordHasher {
    fun getPasswordHash(password: String): String
    fun isPasswordValid(password: String, passwordHash: String): Boolean
}