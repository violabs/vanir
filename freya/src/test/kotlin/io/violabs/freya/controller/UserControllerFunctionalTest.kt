package io.violabs.freya.controller

import io.violabs.freya.TestVariables
import io.violabs.freya.TestVariables.DATE_OF_BIRTH
import io.violabs.freya.TestVariables.JOIN_DATE
import io.violabs.freya.service.UserService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerFunctionalTest(
    @Autowired val client: WebTestClient,
    @Autowired val dbClient: DatabaseClient,
    @Autowired val userService: UserService
) {

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql(TestVariables.DROP_APP_USER_TABLE_QUERY).fetch().awaitOneOrNull()
        dbClient.sql(TestVariables.CREATE_APP_USER_TABLE_QUERY).fetch().awaitOneOrNull()
    }

    @Test
    fun `createUser will create a user with an id`() {
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
    }

    @Test
    fun `updateUser will update a user successfully`() {
        //setup
        val createdId = runBlocking {
            userService.createUser(TestVariables.PRE_SAVED_USER).id!!
        }

        //given
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
}