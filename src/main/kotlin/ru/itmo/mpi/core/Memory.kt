package ru.itmo.mpi.core

import java.time.Instant

data class Memory(
    val id: Int,
    val location: MemoryLocation,
    val date: Instant,
    val type: String,
    val description: String,
    val rating: Int,
    val images: Collection<Int>
)
