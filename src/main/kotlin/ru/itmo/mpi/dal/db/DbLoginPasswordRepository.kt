package ru.itmo.mpi.dal.db

import org.ktorm.database.Database
import org.ktorm.dsl.asIterable
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import ru.itmo.mpi.dal.LoginPasswordRepository

class DbLoginPasswordRepository(private val db: Database) : LoginPasswordRepository {
    override fun getUserIdPassword(login: String): Pair<Int, String>? {
        val row = db
            .from(UserLoginPasswords)
            .select(UserLoginPasswords.userId, UserLoginPasswords.passwordHash)
            .where(UserLoginPasswords.login eq login)
            .asIterable()
            .firstOrNull()
            ?: return null
        return row[UserLoginPasswords.userId]!! to row[UserLoginPasswords.passwordHash]!!
    }
}