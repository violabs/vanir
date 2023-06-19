package io.violabs.freya.service

import io.violabs.freya.domain.Library
import org.springframework.stereotype.Service

@Service
class ReservationDataService(
    private val bookService: BookService,
    private val libraryService: LibraryService,
    private val userService: UserService
) {

    suspend fun getReservationData(userId: Long): Library? {
        return libraryService.getLibraryByUserId(userId)?.also {
            it.book = bookService.getBookById(it.bookId!!)
            it.user = userService.getUserById(userId)
        }
    }
}