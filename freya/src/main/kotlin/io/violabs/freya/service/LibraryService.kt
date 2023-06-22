package io.violabs.freya.service

import io.violabs.freya.domain.AppUser
import io.violabs.freya.domain.Book
import io.violabs.freya.domain.Library
import io.violabs.freya.domain.UserBook
import io.violabs.freya.service.db.BookDbService
import io.violabs.freya.service.db.UserBookDbService
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class LibraryService(
    private val bookDbService: BookDbService,
    private val clock: Clock,
    private val userBookDbService: UserBookDbService,
    private val userDbService: UserDbService
) {

    suspend fun getLibraryDetailsByUserId(userId: Long): Library {
        val user: AppUser? = userDbService.getUserById(userId)

        val books: List<Book> =
            userBookDbService
                .getBookIdsByUserId(userId)
                .mapNotNull(bookDbService::getBookById)
                .toList()

        return Library(user, books)
    }

    suspend fun addBookToLibrary(userId: Long, bookId: Long): Flow<Long> {
        val userBook = UserBook(userId = userId, bookId = bookId, addedOn = clock.instant())

        userBookDbService.createUserBook(userBook)

        return userBookDbService.getBookIdsByUserId(userId)
    }
}