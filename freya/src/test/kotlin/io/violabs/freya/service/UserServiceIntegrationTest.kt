package io.violabs.freya.service

import io.violabs.freya.TestVariables
import io.violabs.freya.TestVariables.MAIN_USER
import io.violabs.freya.TestVariables.PRE_SAVED_USER
import io.violabs.freya.domain.AppUser
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
@Import(UserService::class)
@ExtendWith(SpringExtension::class)
class UserServiceIntegrationTest(
    @Autowired val userService: UserService,
    @Autowired val dbClient: DatabaseClient
) {

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql(TestVariables.DROP_APP_USER_TABLE_QUERY).fetch().awaitOneOrNull()
        dbClient.sql(TestVariables.CREATE_APP_USER_TABLE_QUERY).fetch().awaitOneOrNull()
    }

    @Test
    fun `createUser will add a user successfully`() = runBlocking {
        //when
        val actual: AppUser = userService.createUser(PRE_SAVED_USER)

        println(actual)

        //then
        assertEquals(MAIN_USER, actual)
    }

    @Test
    fun `updateUser will update a user successfully`() = runBlocking {
        //setup
        val createdId = userService.createUser(PRE_SAVED_USER).id!!

        println("CREATED: $createdId")

        //given
        val expected: AppUser = MAIN_USER.copy(email = "newtestuser@test.com")

        //when
        val actual: AppUser = userService.updateUser(expected)

        //then
        assertEquals(expected, actual)
    }

    @Test
    fun `getUserById returns null when the user does not exist`() = runBlocking {
        //when
        val found: AppUser? = userService.getUserById(1)

        //then
        assertEquals(null, found)
    }

    @Test
    fun `getUserById gets the user when it exists`() = runBlocking {
        //setup
        val createdId = userService.createUser(PRE_SAVED_USER).id!!

        println("CREATED: $createdId")

        //when
        val found: AppUser? = userService.getUserById(createdId)

        //then
        assertEquals(MAIN_USER.copy(id = createdId), found!!)
    }

    @Test
    fun `getAllUsers will get a flow of users`() = runBlocking {
        //setup
        (0..2).map { userService.createUser(PRE_SAVED_USER) }.also {
            println("CREATED IDS: $it")
        }

        //when
        val found: List<AppUser> = userService.getAllUsers().toList()

        //then
        assertEquals(listOf(MAIN_USER.copy(id = 1), MAIN_USER.copy(id = 2), MAIN_USER.copy(id = 3)), found)
    }

    @Test
    fun `deleteUserById will delete the user`() = runBlocking {
        //setup
        val createdId = userService.createUser(PRE_SAVED_USER).id!!

        println("CREATED: $createdId")

        //when
        val deleted = userService.deleteUserById(createdId)

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