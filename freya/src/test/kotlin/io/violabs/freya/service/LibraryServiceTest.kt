package io.violabs.freya.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.violabs.core.TestUtils
import io.violabs.freya.domain.AppUser
import io.violabs.freya.domain.Book
import io.violabs.freya.domain.Library
import io.violabs.freya.domain.UserBook
import io.violabs.freya.service.db.BookDbService
import io.violabs.freya.service.db.UserBookDbService
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant

class LibraryServiceTest {
    private val bookDbService: BookDbService = mockk()
    private val clock: Clock = mockk()
    private val userBookDbService: UserBookDbService = mockk()
    private val userDbService: UserDbService = mockk()

    private val libraryService = LibraryService(
        bookDbService,
        clock,
        userBookDbService,
        userDbService
    )

    @Test
    fun `getLibraryDetailsByUserId should return library with books and user`() {
        // given
        val user = AppUser()
        val book1 = Book(id = 1)
        val book2 = Book(id = 2)

        val expected = Library(
            user = user,
            books = listOf(book1, book2)
        )

        // when
        coEvery { userDbService.getUserById(1) } returns user
        coEvery { userBookDbService.getBookIdsByUserId(1) } returns flowOf(1L, 2L, 3L)
        coEvery { bookDbService.getBookById(1) } returns book1
        coEvery { bookDbService.getBookById(2) } returns book2
        coEvery { bookDbService.getBookById(3) } returns null

        val library = runBlocking { libraryService.getLibraryDetailsByUserId(1) }

        // then
        TestUtils.assertEquals(expected, library)
        coVerify { userDbService.getUserById(1) }
        coVerify { userBookDbService.getBookIdsByUserId(1) }
        coVerify { bookDbService.getBookById(1) }
        coVerify { bookDbService.getBookById(2) }
        coVerify { bookDbService.getBookById(3) }
        confirmVerified(userDbService, userBookDbService, bookDbService)
    }

    @Test
    fun `addBookToLibrary will add book`() = runBlocking {
        // given
        val userId = 1L
        val bookId = 2L
        val now = Instant.now()
        val user = UserBook(userId = userId, bookId = bookId, addedOn = now)

        // when
        coEvery { clock.instant() } returns now
        coEvery { userBookDbService.createUserBook(user) } returns user
        coEvery { userBookDbService.getBookIdsByUserId(userId) } returns flowOf(1L, 2L, 3L)

        val bookIds = libraryService.addBookToLibrary(userId, bookId)

        // then
        TestUtils.assertEquals(listOf(1L, 2L, 3L), bookIds.toList())
        coVerify { clock.instant() }
        coVerify { userBookDbService.createUserBook(user) }
        coVerify { userBookDbService.getBookIdsByUserId(userId) }
        confirmVerified(userBookDbService)
    }
}