package io.violabs.freya.controller

import io.violabs.core.TestUtils
import io.violabs.core.domain.UserMessage
import io.violabs.freya.DatabaseTestConfig
import io.violabs.freya.KafkaTestConfig
import io.violabs.freya.TestVariables.User.DATE_OF_BIRTH
import io.violabs.freya.TestVariables.User.JOIN_DATE
import io.violabs.freya.TestVariables.User.PRE_SAVED_USER_1
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@Import(DatabaseTestConfig::class, KafkaTestConfig::class)
class UserControllerFunctionalTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder,
    @Autowired private val userDbService: UserDbService,
    @Autowired private val userKafkaConsumer: KafkaTestConfig.UserKafkaConsumer
) {

    @Test
    fun `createUser will create a user with an id`() = runBlocking {
        testDatabaseSeeder.truncateUser()
        client
            .post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "username": "testuser",
                        "firstname": "Test",
                        "lastname": "User",
                        "email": "testuser@test.com",
                        "dateOfBirth": "$DATE_OF_BIRTH",
                        "joinDate": "$JOIN_DATE"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.username").isEqualTo("testuser")
            .jsonPath("$.firstname").isEqualTo("Test")
            .jsonPath("$.lastname").isEqualTo("User")
            .jsonPath("$.email").isEqualTo("testuser@test.com")
            .jsonPath("$.dateOfBirth").isEqualTo(DATE_OF_BIRTH.toString())
            .jsonPath("$.joinDate").isEqualTo(JOIN_DATE.toString())

        val expected = UserMessage(1, "http://localhost:8080/api/users/1", UserMessage.Type.USER_CREATED)

        val receivedMessage = withTimeoutOrNull(10_000) { userKafkaConsumer.consume() }
        TestUtils.assertEquals(expected, receivedMessage)
    }

    @Test
    fun `updateUser will update a user successfully`() {
        //setup
        val createdId = createUser()

        //when
        client
            .put()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "id": "$createdId",
                        "username": "testuser",
                        "firstname": "Test",
                        "lastname": "User",
                        "email": "newtestuser@test.com",
                        "dateOfBirth": "$DATE_OF_BIRTH",
                        "joinDate": "$JOIN_DATE"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .jsonPath("$.id").isEqualTo(createdId)
            .jsonPath("$.username").isEqualTo("testuser")
            .jsonPath("$.firstname").isEqualTo("Test")
            .jsonPath("$.lastname").isEqualTo("User")
            .jsonPath("$.email").isEqualTo("newtestuser@test.com")
            .jsonPath("$.dateOfBirth").isEqualTo(DATE_OF_BIRTH.toString())
            .jsonPath("$.joinDate").isEqualTo(JOIN_DATE.toString())
    }

    @Test
    fun `getUserById will get user when it exists`() {
        //setup
        val createdId = createUser()

        //when
        client
            .get()
            .uri("/api/users/$createdId")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .jsonPath("$.id").isEqualTo(createdId)
            .jsonPath("$.username").isEqualTo("testuser")
            .jsonPath("$.firstname").isEqualTo("Test")
            .jsonPath("$.lastname").isEqualTo("User")
            .jsonPath("$.email").isEqualTo("testuser@test.com")
            .jsonPath("$.dateOfBirth").isEqualTo(DATE_OF_BIRTH.toString())
            .jsonPath("$.joinDate").isEqualTo(JOIN_DATE.toString())
    }

    @Test
    fun `getUserById will return not found when user does not exist`() {
        // setup
        testDatabaseSeeder.truncateUser()

        // expect
        client
            .get()
            .uri("/api/users/1")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `deleteUserById will delete user when it exists`() {
        //setup
        val createdId = createUser()

        //given
        client
            .delete()
            .uri("/api/users/$createdId")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    private fun createUser(): Long {
        testDatabaseSeeder.truncateUser()
        return runBlocking {
            userDbService.createUser(PRE_SAVED_USER_1).id!!
        }
    }
}