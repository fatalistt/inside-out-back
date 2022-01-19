package ru.itmo.mpi.api.routes

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.utils.io.core.readBytes
import org.koin.ktor.ext.inject
import ru.itmo.mpi.core.Memory
import ru.itmo.mpi.core.MemoryLocation
import ru.itmo.mpi.core.toInstant
import ru.itmo.mpi.dal.FileRepository
import ru.itmo.mpi.dal.MemoryRepository
import java.time.Instant
import java.time.OffsetDateTime

data class SaveMemoryRequest(
    val date: OffsetDateTime,
    val type: String,
    val desc: String,
    val rating: Int,
    val images: Collection<Int>
)

data class SaveMemoryResponse(val resultCode: Int, val message: Collection<String>)

data class GetMemoriesRequest(
    val date: Instant?,
    val type: String?,
    val dateFrom: Instant?,
    val dateTo: Instant?,
    val ratingFrom: Int?,
    val ratingTo: Int?
) {
    companion object {
        fun fromQueryParameters(parameters: Parameters): GetMemoriesRequest {
            val date = parameters["date"]?.toInstant()
            val type = parameters["type"]
            val dateFrom = parameters["dateFrom"]?.toInstant()
            val dateTo = parameters["dateTo"]?.toInstant()
            val ratingFrom = parameters["ratingFrom"]?.toInt()
            val ratingTo = parameters["ratingTo"]?.toInt()
            return GetMemoriesRequest(date, type, dateFrom, dateTo, ratingFrom, ratingTo)
        }
    }
}

data class MemoryResponse(
    val id: Int,
    val date: Instant,
    val images: Collection<Int>,
    val description: String,
    val memoryLocation: MemoryLocation,
    val rating: Int
) {
    companion object {
        fun fromMemory(memory: Memory): MemoryResponse = MemoryResponse(
            memory.id,
            memory.date,
            memory.images,
            memory.description,
            memory.location,
            memory.rating
        )
    }
}

data class GetMemoriesResponse(val items: Collection<MemoryResponse>, val totalCount: Int)

data class TransportMemoryRequest(val id: Int, val destination: MemoryLocation)
data class TransportMemoryResponse(val resultCode: Int, val message: Collection<String>)

data class DeleteMemoryResponse(val resultCode: Int, val message: Collection<String>)

data class MemoryImageUploadResponse(val resultCode: Int, val imagesId: Collection<Int>)

fun Application.memoryRoute() {
    routing {
        route("/memories") {
            val memoryRepository: MemoryRepository by inject()
            val fileRepository: FileRepository by inject()

            post("/saveMemory") {
                val (date, type, desc, rating, images) = call.receive<SaveMemoryRequest>()
                memoryRepository.saveShortMemory(date.toInstant(), type, desc, rating, images)

                val response = SaveMemoryResponse(0, emptyList())
                call.respond(response)
            }
            get("/memory") {
                val (date, type, dateFrom, dateTo, ratingFrom, ratingTo) = GetMemoriesRequest
                    .fromQueryParameters(call.request.queryParameters)
                if (date !== null && (dateFrom !== null || dateTo !== null)) {
                    call.respondText("specify only one of `date` or `dateFrom` with `dateTo`")
                    return@get
                }

                val memories = memoryRepository.getShortMemories(date, type, dateFrom, dateTo, ratingFrom, ratingTo)

                val response = GetMemoriesResponse(memories.map(MemoryResponse.Companion::fromMemory), memories.size)
                call.respond(response)
            }
            post("/transportMemory") {
                val (id, destination) = call.receive<TransportMemoryRequest>()
                memoryRepository.transferMemoryToLocation(id, destination)

                val response = TransportMemoryResponse(0, emptyList())
                call.respond(response)
            }
            delete("/memory/{id}") {
                val id = call.parameters["id"]?.toInt()
                val response = if (id === null) {
                    DeleteMemoryResponse(1, listOf("id required"))
                } else {
                    memoryRepository.deleteMemory(id)
                    DeleteMemoryResponse(0, emptyList())
                }

                call.respond(response)
            }
            post("/image/upload") {
                val request = call.receiveMultipart()
                val ids = mutableListOf<Int>()
                request.forEachPart { part ->
                    val contentType = part.contentType?.toString()
                    if (part is PartData.FileItem && contentType !== null) {
                        part.provider().use {
                            val bytes = it.readBytes()
                            val id = fileRepository.saveFile(bytes, contentType)
                            ids.add(id)
                        }
                    }
                }

                val response = MemoryImageUploadResponse(0, ids)
                call.respond(response)
            }
            get("/image/download/{id}") {
                val id = call.parameters["id"]?.toInt()
                if (id === null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val image = fileRepository.getFile(id)
                if (image === null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondBytes(image.first, ContentType.parse(image.second))
            }
        }
    }
}
