package io.violabs.freya.controller

import io.violabs.freya.domain.Book
import io.violabs.freya.service.db.BookDbService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/books")
class BookController(private val bookService: BookDbService) {
    @PostMapping
    suspend fun createBook(@RequestBody book: Book): Book = bookService.createBook(book)

    @GetMapping("{id}")
    suspend fun getBookById(@PathVariable id: Long): Book? = bookService.getBookById(id)

    @GetMapping
    fun getBooks(): Flow<Book> = bookService.listBooks()
}