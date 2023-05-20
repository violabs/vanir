package io.violabs.freya.service

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
import java.time.LocalDate

@DataR2dbcTest
@Import(UserService::class)
@ExtendWith(SpringExtension::class)
class UserRepositoryTest(
    @Autowired val userService: UserService,
    @Autowired val dbClient: DatabaseClient
) {
    private val createAppUserTableQuery = """
        CREATE TABLE IF NOT EXISTS app_user (
            id SERIAL PRIMARY KEY,
            username VARCHAR(255) NOT NULL,
            firstname VARCHAR(255) NOT NULL,
            lastname VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            date_of_birth TIMESTAMP,
            join_date TIMESTAMP
        );
    """.trimIndent()

    private val mainUser = AppUser(
        id = 1,
        username = "testuser",
        firstname = "Test",
        lastname = "User",
        email = "testuser@test.com",
        dateOfBirth = LocalDate.now().minusYears(20),
        joinDate = LocalDate.now()
    )

    private val preSavedUser = mainUser.copy(id = null)

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql("DROP TABLE IF EXISTS app_user").fetch().awaitOneOrNull()
        dbClient.sql(createAppUserTableQuery).fetch().awaitOneOrNull()
    }

    @Test
    fun `createUser will add a user successfully`() = runBlocking {
        //when
        val actual: AppUser = userService.createUser(preSavedUser)

        println(actual)

        //then
        assertEquals(mainUser, actual)
    }

    @Test
    fun `updateUser will update a user successfully`() = runBlocking {
        //setup
        val createdId = userService.createUser(preSavedUser).id!!

        println("CREATED: $createdId")

        //given
        val expected: AppUser = mainUser.copy(email = "newtestuser@test.com")

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
        val createdId = userService.createUser(preSavedUser).id!!

        println("CREATED: $createdId")

        //when
        val found: AppUser? = userService.getUserById(createdId)

        //then
        assertEquals(mainUser.copy(id = createdId), found!!)
    }

    @Test
    fun `getAllUsers will get a flow of users`() = runBlocking {
        //setup
        (0..2).map { userService.createUser(preSavedUser) }.also {
            println("CREATED IDS: $it")
        }

        //when
        val found: List<AppUser> = userService.getAllUsers().toList()

        //then
        assertEquals(listOf(mainUser.copy(id = 1), mainUser.copy(id = 2), mainUser.copy(id = 3)), found)
    }

    @Test
    fun `deleteUserById will delete the user`() = runBlocking {
        //setup
        val createdId = userService.createUser(preSavedUser).id!!

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