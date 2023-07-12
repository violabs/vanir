package io.violabs.freya.service.db

import io.violabs.freya.domain.Book
import io.violabs.freya.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class BookDbService(private val bookRepository: BookRepository) {
    suspend fun createBook(book: Book): Book = bookRepository.save(book)

    suspend fun getBookById(id: Long): Book? = bookRepository.findById(id)

    fun listBooks(): Flow<Book> = bookRepository.findAll()
}