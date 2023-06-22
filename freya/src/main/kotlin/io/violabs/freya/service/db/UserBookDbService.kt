package io.violabs.freya.service.db

import io.violabs.freya.domain.UserBook
import io.violabs.freya.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserBookDbService(private val userBookRepository: UserBookRepository) {
    suspend fun createUserBook(userBook: UserBook): UserBook {
        val exists: Boolean = userBookRepository.existsByUserIdAndBookId(userBook.userId!!, userBook.bookId!!) ?: false

        if (exists) throw IllegalArgumentException("UserBook already exists")

        return userBookRepository.save(userBook)
    }
    suspend fun getUserBookById(id: Long): UserBook? = userBookRepository.findById(id)

    fun getBookIdsByUserId(userId: Long): Flow<Long> = userBookRepository.getBookIdsByUserId(userId)
}