package io.violabs.freya.service

import io.violabs.freya.TestVariables
import io.violabs.freya.TestVariables.MAIN_BOOK
import io.violabs.freya.TestVariables.PRE_SAVED_BOOK
import io.violabs.freya.domain.Book
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataR2dbcTest
@Import(BookService::class)
@ExtendWith(SpringExtension::class)
class BookServiceIntegrationTest(
    @Autowired val bookService: BookService,
    @Autowired val dbClient: DatabaseClient
) {

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql(TestVariables.DROP_BOOK_TABLE_QUERY).fetch().awaitOneOrNull()
        dbClient.sql(TestVariables.CREATE_BOOK_TABLE_QUERY).fetch().awaitOneOrNull()
    }

    @Test
    fun `createBook will add a user successfully`() = runBlocking {
        //when
        val actual: Book = bookService.createBook(PRE_SAVED_BOOK)

        println(actual)

        //then
        assertEquals(MAIN_BOOK, actual)
    }

    @Test
    fun `getBookById returns null when the user does not exist`() = runBlocking {
        //when
        val found: Book? = bookService.getBookById(1)

        //then
        assertEquals(null, found)
    }

    @Test
    fun `getBookById gets the user when it exists`() = runBlocking {
        //setup
        val createdId = bookService.createBook(PRE_SAVED_BOOK).id!!

        println("CREATED: $createdId")

        //when
        val found: Book? = bookService.getBookById(createdId)

        //then
        assertEquals(MAIN_BOOK.copy(id = createdId), found!!)
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