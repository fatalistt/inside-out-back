package ru.itmo.mpi.dal

import ru.itmo.mpi.core.Memory
import ru.itmo.mpi.core.MemoryLocation
import java.time.Instant

interface MemoryRepository {
    fun saveShortMemory(date: Instant, type: String, description: String, rating: Int, images: Collection<Int>): Int

    fun getShortMemories(
        date: Instant?,
        type: String?,
        dateFrom: Instant?,
        dateTo: Instant?,
        ratingFrom: Int?,
        ratingTo: Int?
    ): Collection<Memory>

    fun transferMemoryToLocation(id: Int, location: MemoryLocation)

    fun deleteMemory(id: Int)
}