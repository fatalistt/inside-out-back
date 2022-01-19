package ru.itmo.mpi.dal.db

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar

abstract class MemoryTable(tableName: String) : Table<Nothing>(tableName) {
    val memoryId = int("id").primaryKey()
    val date = timestamp("date")
    val type = varchar("type")
    val description = varchar("description")
    val rating = int("rating")
}

object ShortMemories : MemoryTable("short_memory")
object LongMemories : MemoryTable("long_memory")

abstract class MemoryFileTable(tableName: String) : Table<Nothing>(tableName) {
    val id = long("id").primaryKey()
    val memoryId = int("memory_id")
    val fileId = int("file_id")
}

object ShortMemoriesFiles : MemoryFileTable("short_memory_file")
object LongMemoriesFiles : MemoryFileTable("long_memory_file")
