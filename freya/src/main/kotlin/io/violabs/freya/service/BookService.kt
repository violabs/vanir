package io.violabs.freya.service

import io.violabs.freya.domain.Book
import io.violabs.freya.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(private val bookRepository: BookRepository) {
    suspend fun createBook(book: Book): Book = bookRepository.save(book)

    suspend fun getBookById(id: Long): Book? = bookRepository.findById(id)
}