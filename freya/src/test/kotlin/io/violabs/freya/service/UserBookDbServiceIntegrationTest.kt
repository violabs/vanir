package io.violabs.freya.service

import io.violabs.core.TestUtils
import io.violabs.freya.DatabaseTestConfig
import io.violabs.freya.TestVariables.UserBook.PRE_SAVED_USER_BOOK_1
import io.violabs.freya.TestVariables.UserBook.USER_BOOK_1
import io.violabs.freya.domain.UserBook
import io.violabs.freya.service.db.UserBookDbService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataR2dbcTest
@ExtendWith(SpringExtension::class)
@Import(UserBookDbService::class, DatabaseTestConfig::class)
class UserBookDbServiceIntegrationTest(
    @Autowired val userBookDbService: UserBookDbService,
    @Autowired val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder
) {

    @Test
    fun `createUserBook will add a userBook successfully`() = runBlocking {
        //when
        val actual: UserBook = userBookDbService.createUserBook(PRE_SAVED_USER_BOOK_1)

        //then
        TestUtils.assertEquals(USER_BOOK_1, actual)
    }

    @Test
    fun `getUserBookById returns null when the userBook does not exist`() = runBlocking {
        //setup
        testDatabaseSeeder.truncateUserBook()

        //when
        val found: UserBook? = userBookDbService.getUserBookById(1)

        //then
        TestUtils.assertEquals(null, found)
    }

    @Test
    fun `getUserBookById gets the userBook when it exists`() = runBlocking {
        //setup
        val id: Long = userBookDbService.createUserBook(PRE_SAVED_USER_BOOK_1).id!!

        //when
        val found: UserBook? = userBookDbService.getUserBookById(id)

        //then
        TestUtils.assertEquals(USER_BOOK_1.copy(id = id), found!!)
    }

    @Test
    fun `getBookIdsByUserId will find userBooks`() = runBlocking {
        //setup
        testDatabaseSeeder.seedUserBook()

        //when
        val ids: List<Long> = userBookDbService.getBookIdsByUserId(1).toList()

        //then
        TestUtils.assertEquals(listOf(1L, 2L), ids)
    }
}