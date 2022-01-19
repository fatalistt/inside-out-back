package ru.itmo.mpi.dal.db

import org.ktorm.database.Database
import org.ktorm.dsl.asIterable
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import ru.itmo.mpi.dal.FileRepository

class DbFileRepository(private val db: Database) : FileRepository {
    override fun saveFile(content: ByteArray, contentType: String): Int {
        db.useTransaction { tx ->
            val fileId = db.insertAndGenerateKey(Files) {
                set(it.content, content)
                set(it.contentType, contentType)
            }

            tx.commit()

            return fileId as Int
        }
    }

    override fun getFile(id: Int): Pair<ByteArray, String>? {
        val row = db
            .from(Files)
            .select(Files.content, Files.contentType)
            .where(Files.fileId eq id)
            .asIterable()
            .firstOrNull()
            ?: return null
        return Pair(row[Files.content]!!, row[Files.contentType]!!)
    }
}