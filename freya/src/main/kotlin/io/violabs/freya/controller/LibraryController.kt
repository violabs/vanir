package io.violabs.freya.controller

import io.violabs.freya.domain.Library
import io.violabs.freya.service.LibraryService
import mu.KLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/libraries")
class LibraryController(private val libraryService: LibraryService) {
    @GetMapping("{userId}")
    suspend fun getLibraryDetailsByUserId(@PathVariable userId: Long): Library =
        log("getLibraryDetailsByUserId($userId)") { libraryService.getLibraryDetailsByUserId(userId) }

    private suspend fun <T> log(message: String, fn: suspend () -> T): T {
        logger.info { "STARTING: $message" }
        return fn().also { logger.info { "FINISHING: $message" } }
    }

    companion object : KLogging()
}