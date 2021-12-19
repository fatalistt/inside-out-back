package ru.itmo.mpi.dal.db

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserLoginPasswords : Table<Nothing>("user_login_password") {
    val userId = int("id").primaryKey()
    val login = varchar("login")
    val passwordHash = varchar("password_hash")
}
