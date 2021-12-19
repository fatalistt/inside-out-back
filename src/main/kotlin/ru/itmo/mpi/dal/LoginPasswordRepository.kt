package ru.itmo.mpi.dal

interface LoginPasswordRepository {
    fun getUserIdPassword(login: String): Pair<Int, String>?
}