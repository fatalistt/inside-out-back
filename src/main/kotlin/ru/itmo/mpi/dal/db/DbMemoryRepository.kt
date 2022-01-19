package ru.itmo.mpi.dal.db

import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.asc
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.greaterEq
import org.ktorm.dsl.inList
import org.ktorm.dsl.insert
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.isNotNull
import org.ktorm.dsl.less
import org.ktorm.dsl.map
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.schema.ColumnDeclaring
import ru.itmo.mpi.core.Memory
import ru.itmo.mpi.core.MemoryLocation
import ru.itmo.mpi.dal.MemoryRepository
import java.time.Instant

class DbMemoryRepository(private val db: Database) : MemoryRepository {
    override fun saveShortMemory(
        date: Instant,
        type: String,
        description: String,
        rating: Int,
        images: Collection<Int>
    ): Int {
        val memoryId = db.insertAndGenerateKey(ShortMemories) {
            set(ShortMemories.date, date)
            set(ShortMemories.type, type)
            set(ShortMemories.description, description)
            set(ShortMemories.rating, rating)
        } as Int

        if (images.isNotEmpty()) {
            db.batchInsert(ShortMemoriesFiles) {
                for (image in images) {
                    item {
                        set(it.fileId, image)
                        set(it.memoryId, memoryId)
                    }
                }
            }
        }

        return memoryId
    }

    override fun getShortMemories(
        date: Instant?,
        type: String?,
        dateFrom: Instant?,
        dateTo: Instant?,
        ratingFrom: Int?,
        ratingTo: Int?
    ): Collection<Memory> =
        getMemories(ShortMemories, ShortMemoriesFiles, id = null, date, type, dateFrom, dateTo, ratingFrom, ratingTo)

    private fun getMemories(
        memoryTable: MemoryTable,
        fileTable: MemoryFileTable,
        id: Int? = null,
        date: Instant? = null,
        type: String? = null,
        dateFrom: Instant? = null,
        dateTo: Instant? = null,
        ratingFrom: Int? = null,
        ratingTo: Int? = null
    ): Collection<Memory> {
        val memories = db.from(memoryTable)
            .select(
                memoryTable.memoryId,
                memoryTable.date,
                memoryTable.type,
                memoryTable.description,
                memoryTable.rating
            )
            .where {
                val conditions = ArrayList<ColumnDeclaring<Boolean>>()

                conditions += memoryTable.memoryId.isNotNull()

                if (id !== null) {
                    conditions += memoryTable.memoryId eq id
                }

                if (date !== null) {
                    conditions += memoryTable.date eq date
                }

                if (dateFrom !== null) {
                    conditions += memoryTable.date greaterEq dateFrom
                }

                if (dateTo !== null) {
                    conditions += memoryTable.date less dateTo
                }

                if (type !== null) {
                    conditions += memoryTable.type eq type
                }

                if (ratingFrom !== null) {
                    conditions += memoryTable.rating greaterEq ratingFrom
                }

                if (ratingTo !== null) {
                    conditions += memoryTable.rating less ratingTo
                }

                conditions.reduce { a, b -> a and b }
            }
            .orderBy(memoryTable.memoryId.asc())
            .map { row ->
                object {
                    val id = row[memoryTable.memoryId]!!
                    val date = row[memoryTable.date]!!
                    val type = row[memoryTable.type]!!
                    val description = row[memoryTable.description]!!
                    val rating = row[memoryTable.rating]!!
                }
            }
        if (memories.isEmpty()) {
            return emptyList()
        }
        val rawFiles = db.from(fileTable)
            .select(fileTable.memoryId, fileTable.fileId)
            .where(fileTable.memoryId inList memories.map { it.id })
            .orderBy(fileTable.memoryId.asc(), fileTable.fileId.asc())
            .map { row ->
                object {
                    val memoryId = row[fileTable.memoryId]!!
                    val fileId = row[fileTable.fileId]!!
                }
            }
        val files = HashMap<Int, MutableList<Int>>(rawFiles.count())
        for (file in rawFiles) {
            val list = files.getOrPut(file.memoryId) { mutableListOf() }
            list.add(file.fileId)
        }
        return memories.map {
            Memory(
                it.id,
                MemoryLocation.ShortMemory,
                it.date,
                it.type,
                it.description,
                it.rating,
                files.getOrDefault(it.id, emptyList())
            )
        }
    }

    override fun transferMemoryToLocation(id: Int, location: MemoryLocation) {
        val srcMemoryTable: MemoryTable
        val srcFileTable: MemoryFileTable
        val dstMemoryTable: MemoryTable
        val dstFileTable: MemoryFileTable
        if (location === MemoryLocation.ShortMemory) {
            srcMemoryTable = LongMemories
            srcFileTable = LongMemoriesFiles
            dstMemoryTable = ShortMemories
            dstFileTable = ShortMemoriesFiles
        } else {
            dstMemoryTable = LongMemories
            dstFileTable = LongMemoriesFiles
            srcMemoryTable = ShortMemories
            srcFileTable = ShortMemoriesFiles
        }

        db.useTransaction { tx ->
            val old = getMemories(srcMemoryTable, srcFileTable, id = id).firstOrNull()
                ?: throw Exception("Воспоминание не найдено")
            db.insert(dstMemoryTable) {
                set(it.memoryId, old.id)
                set(it.date, old.date)
                set(it.type, old.type)
                set(it.description, old.description)
                set(it.rating, old.rating)
            }
            if (old.images.isNotEmpty()) {
                db.batchInsert(dstFileTable) {
                    for (image in old.images) {
                        item {
                            set(it.memoryId, old.id)
                            set(it.fileId, image)
                        }
                    }
                }
                db.delete(srcFileTable) { it.memoryId eq old.id }
            }
            db.delete(srcMemoryTable) { it.memoryId eq old.id }

            tx.commit()
        }
    }

    override fun deleteMemory(id: Int) {
        db.useTransaction { tx ->
            db.delete(ShortMemoriesFiles) { it.memoryId eq id }
            db.delete(ShortMemories) { it.memoryId eq id }
            db.delete(LongMemoriesFiles) { it.memoryId eq id }
            db.delete(LongMemories) { it.memoryId eq id }
            tx.commit()
        }
    }
}