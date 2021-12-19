package ru.itmo.mpi.dal.db

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Users : Table<Nothing>("user") {
    val id = int("id").primaryKey()
    val name = varchar("name")
}

object Roles : Table<Nothing>("role") {
    val id = int("id").primaryKey()
    val name = varchar("name")
}

object UserRoles : Table<Nothing>("user_role") {
    @Suppress("unused")
    val id = int("id").primaryKey()
    val userId = int("user_id")
    val roleId = int("role_id")
}
