package io.violabs.freya.service.db

import io.violabs.core.TestUtils
import io.violabs.freya.DatabaseTestConfig
import io.violabs.freya.TestVariables.Book.BOOK_1
import io.violabs.freya.TestVariables.Book.PRE_SAVED_BOOK_1
import io.violabs.freya.domain.Book
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataR2dbcTest
@ExtendWith(SpringExtension::class)
@Import(BookDbService::class, DatabaseTestConfig::class)
class BookDbServiceIntegrationTest(
    @Autowired val bookDbService: BookDbService,
    @Autowired val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder
) {

    @Test
    fun `createBook will add a user successfully`() = runBlocking {
        //given
        testDatabaseSeeder.truncateBook()

        //when
        val actual: Book = bookDbService.createBook(PRE_SAVED_BOOK_1)

        //then
        TestUtils.assertEquals(BOOK_1, actual)
    }

    @Test
    fun `getBookById returns null when the user does not exist`() = runBlocking {
        //given
        testDatabaseSeeder.truncateBook()

        //when
        val found: Book? = bookDbService.getBookById(1)

        //then
        TestUtils.assertEquals(null, found)
    }

    @Test
    fun `getBookById gets the user when it exists`() = runBlocking {
        //setup
        testDatabaseSeeder.seedBook()

        //when
        val found: Book? = bookDbService.getBookById(1)

        //then
        TestUtils.assertEquals(BOOK_1.copy(id = 1), found!!)
    }
}