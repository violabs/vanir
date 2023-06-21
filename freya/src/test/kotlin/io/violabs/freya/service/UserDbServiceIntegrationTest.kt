package io.violabs.freya.service

import io.violabs.freya.TestVariables.User.CREATE_APP_USER_TABLE_QUERY
import io.violabs.freya.TestVariables.User.DROP_APP_USER_TABLE_QUERY
import io.violabs.freya.TestVariables.User.USER_1
import io.violabs.freya.TestVariables.User.PRE_SAVED_USER_1
import io.violabs.freya.domain.AppUser
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.flow.toList
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
@Import(UserDbService::class)
@ExtendWith(SpringExtension::class)
class UserDbServiceIntegrationTest(
    @Autowired val userDbService: UserDbService,
    @Autowired val dbClient: DatabaseClient
) {

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql(DROP_APP_USER_TABLE_QUERY).fetch().awaitOneOrNull()
        dbClient.sql(CREATE_APP_USER_TABLE_QUERY).fetch().awaitOneOrNull()
    }

    @Test
    fun `createUser will add a user successfully`() = runBlocking {
        //when
        val actual: AppUser = userDbService.createUser(PRE_SAVED_USER_1)

        println(actual)

        //then
        assertEquals(USER_1, actual)
    }

    @Test
    fun `updateUser will update a user successfully`() = runBlocking {
        //setup
        val createdId = userDbService.createUser(PRE_SAVED_USER_1).id!!

        println("CREATED: $createdId")

        //given
        val expected: AppUser = USER_1.copy(email = "newtestuser@test.com")

        //when
        val actual: AppUser = userDbService.updateUser(expected)

        //then
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserById returns null when the user does not exist`() = runBlocking {
        //when
        val found: AppUser? = userDbService.getUserById(1)

        //then
        assertEquals(null, found)
    }

    @Test
    fun `getUserById gets the user when it exists`() = runBlocking {
        //setup
        val createdId = userDbService.createUser(PRE_SAVED_USER_1).id!!

        println("CREATED: $createdId")

        //when
        val found: AppUser? = userDbService.getUserById(createdId)

        //then
        assertEquals(USER_1.copy(id = createdId), found!!)
    }

    @Test
    fun `getAllUsers will get a flow of users`() = runBlocking {
        //setup
        (0..2).map { userDbService.createUser(PRE_SAVED_USER_1) }.also {
            println("CREATED IDS: $it")
        }

        //when
        val found: List<AppUser> = userDbService.getAllUsers().toList()

        //then
        assertEquals(listOf(USER_1.copy(id = 1), USER_1.copy(id = 2), USER_1.copy(id = 3)), found)
    }

    @Test
    fun `deleteUserById will delete the user`() = runBlocking {
        //setup
        val createdId = userDbService.createUser(PRE_SAVED_USER_1).id!!

        println("CREATED: $createdId")

        //when
        val deleted = userDbService.deleteUserById(createdId)

        //then
        assert(deleted)
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