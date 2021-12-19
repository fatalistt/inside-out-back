package ru.itmo.mpi.dal.db

import org.ktorm.database.Database
import org.ktorm.dsl.asIterable
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.mapNotNull
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import ru.itmo.mpi.core.User
import ru.itmo.mpi.dal.UserRepository

class DbUserRepository(private val db: Database) : UserRepository {
    override fun getUser(id: Int): User? {
        val userRow = db
            .from(Users)
            .select()
            .where(Users.id eq id)
            .asIterable()
            .firstOrNull()
            ?: return null
        val roles = db
            .from(UserRoles)
            .innerJoin(Roles, UserRoles.roleId eq Roles.id)
            .select(Roles.name)
            .where { UserRoles.userId eq id }
            .mapNotNull { row -> row[Roles.name] }
        return User(id, userRow[Users.name]!!, roles)
    }
}