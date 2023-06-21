package io.violabs.freya.service

import io.violabs.freya.TestVariables.Book.CREATE_BOOK_TABLE_QUERY
import io.violabs.freya.TestVariables.Book.DROP_BOOK_TABLE_QUERY
import io.violabs.freya.TestVariables.Book.BOOK_1
import io.violabs.freya.TestVariables.Book.PRE_SAVED_BOOK_1
import io.violabs.freya.domain.Book
import io.violabs.freya.service.db.BookDbService
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
@Import(BookDbService::class)
@ExtendWith(SpringExtension::class)
class BookDbServiceIntegrationTest(
    @Autowired val bookDbService: BookDbService,
    @Autowired val dbClient: DatabaseClient
) {

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql(DROP_BOOK_TABLE_QUERY).fetch().awaitOneOrNull()
        dbClient.sql(CREATE_BOOK_TABLE_QUERY).fetch().awaitOneOrNull()
    }

    @Test
    fun `createBook will add a user successfully`() = runBlocking {
        //when
        val actual: Book = bookDbService.createBook(PRE_SAVED_BOOK_1)

        println(actual)

        //then
        assertEquals(BOOK_1, actual)
    }

    @Test
    fun `getBookById returns null when the user does not exist`() = runBlocking {
        //when
        val found: Book? = bookDbService.getBookById(1)

        //then
        assertEquals(null, found)
    }

    @Test
    fun `getBookById gets the user when it exists`() = runBlocking {
        //setup
        val createdId = bookDbService.createBook(PRE_SAVED_BOOK_1).id!!

        println("CREATED: $createdId")

        //when
        val found: Book? = bookDbService.getBookById(createdId)

        //then
        assertEquals(BOOK_1.copy(id = createdId), found!!)
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