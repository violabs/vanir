package io.violabs.freya.controller

import io.violabs.freya.domain.Library
import io.violabs.freya.service.LibraryService
import kotlinx.coroutines.flow.Flow
import mu.KLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/libraries")
class LibraryController(private val libraryService: LibraryService) {
    @GetMapping("{userId}")
    suspend fun getLibraryDetailsByUserId(@PathVariable userId: Long): Library =
        log("getLibraryDetailsByUserId($userId)") { libraryService.getLibraryDetailsByUserId(userId) }

    @PostMapping("{userId}/book/{bookId}")
    suspend fun addBookToLibrary(@PathVariable userId: Long, @PathVariable bookId: Long): Flow<Long> =
        log("addBookToLibrary($userId, $bookId)") { libraryService.addBookToLibrary(userId, bookId) }

    private suspend fun <T> log(message: String, fn: suspend () -> T): T {
        logger.info { "STARTING: $message" }
        return fn().also { logger.info { "FINISHING: $message" } }
    }

    companion object : KLogging()
}