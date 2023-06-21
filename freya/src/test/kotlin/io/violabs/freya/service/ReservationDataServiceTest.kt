package io.violabs.freya.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.violabs.freya.domain.AppUser
import io.violabs.freya.domain.Book
import io.violabs.freya.domain.Library
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

class ReservationDataServiceTest {
    private val bookService: BookService = mockk()
    private val libraryService: LibraryService = mockk()
    private val userService: UserService = mockk()

    private val reservationDataService = ReservationDataService(
        bookService,
        libraryService,
        userService
    )

    @Test
    fun `getReservationData should return library with book and user`() {
        // given
        val userId = 1L
        val library = Library(
            id = 1L,
            userId = userId,
            bookId = 1L,
            addedOn = Instant.now()
        )
        val book = Book(
            id = 1L,
            title = "title",
            author = "author"
        )
        val user = AppUser(
            id = userId,
            username = "username"
        )

        val expected = library.copy().also {
            it.book = book
            it.user = user
        }

        coEvery { libraryService.getLibraryByUserId(userId) } returns library
        coEvery { bookService.getBookById(library.bookId!!) } returns book
        coEvery { userService.getUserById(userId) } returns user

        // when
        val result = runBlocking { reservationDataService.getReservationData(userId) }

        // then
        coVerify { libraryService.getLibraryByUserId(userId) }
        coVerify { bookService.getBookById(library.bookId!!) }
        coVerify { userService.getUserById(userId) }
        confirmVerified(libraryService, bookService, userService)
        assertEquals(expected, result)
    }

    private fun <T> assertEquals(expected: T, actual: T) {
        assert(expected == actual) {
            """
               | EXPECT: $expected
               | ACTUAL: $actual
            """.trimMargin()
        }
    }
}