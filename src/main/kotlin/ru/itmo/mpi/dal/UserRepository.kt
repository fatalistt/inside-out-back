package ru.itmo.mpi.dal

import ru.itmo.mpi.core.User

interface UserRepository {
    fun getUser(id: Int): User?
}