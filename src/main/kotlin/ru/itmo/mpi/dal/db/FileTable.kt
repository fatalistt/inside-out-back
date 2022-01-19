package ru.itmo.mpi.dal.db

import org.ktorm.schema.Table
import org.ktorm.schema.bytes
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Files : Table<Nothing>("file") {
    val fileId = int("id").primaryKey()
    val contentType = varchar("content_type")
    val content = bytes("content")
}