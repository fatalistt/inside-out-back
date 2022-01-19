package ru.itmo.mpi.dal

interface FileRepository {
    fun saveFile(content: ByteArray, contentType: String): Int
    fun getFile(id: Int): Pair<ByteArray, String>?
}