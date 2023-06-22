package io.violabs.freya.service

import io.violabs.core.TestUtils
import io.violabs.freya.DatabaseTestConfig
import io.violabs.freya.TestVariables.User.PRE_SAVED_USER_1
import io.violabs.freya.TestVariables.User.USER_1
import io.violabs.freya.TestVariables.User.USER_2
import io.violabs.freya.domain.AppUser
import io.violabs.freya.service.db.UserDbService
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
@Import(UserDbService::class, DatabaseTestConfig::class)
class UserDbServiceIntegrationTest(
    @Autowired val userDbService: UserDbService,
    @Autowired val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder
) {

    @Test
    fun `createUser will add a user successfully`() = runBlocking {
        //when
        val actual: AppUser = userDbService.createUser(PRE_SAVED_USER_1)

        println(actual)

        //then
        TestUtils.assertEquals(USER_1, actual)
    }

    @Test
    fun `updateUser will update a user successfully`() = runBlocking {
        //setup
        testDatabaseSeeder.seedUser()

        //given
        val expected: AppUser = USER_1.copy(email = "newtestuser@test.com")

        //when
        val actual: AppUser = userDbService.updateUser(expected)

        //then
        TestUtils.assertEquals(expected, actual)
    }

    @Test
    fun `getUserById returns null when the user does not exist`() = runBlocking {
        //given
        testDatabaseSeeder.truncateUser()

        //when
        val found: AppUser? = userDbService.getUserById(1)

        //then
        TestUtils.assertEquals(null, found)
    }

    @Test
    fun `getUserById gets the user when it exists`() = runBlocking {
        //setup
        testDatabaseSeeder.seedUser()

        //when
        val found: AppUser? = userDbService.getUserById(1)

        //then
        TestUtils.assertEquals(USER_1.copy(id = 1), found!!)
    }

    @Test
    fun `getAllUsers will get a flow of users`() = runBlocking {
        //setup
        testDatabaseSeeder.seedUser()

        //when
        val found: List<AppUser> = userDbService.getAllUsers().toList()

        //then
        TestUtils.assertEquals(listOf(USER_1.copy(id = 1), USER_2.copy(id = 2)), found)
    }

    @Test
    fun `deleteUserById will delete the user`() = runBlocking {
        //setup
        testDatabaseSeeder.seedUser()

        //when
        val deleted = userDbService.deleteUserById(1)

        //then
        assert(deleted)
    }
}