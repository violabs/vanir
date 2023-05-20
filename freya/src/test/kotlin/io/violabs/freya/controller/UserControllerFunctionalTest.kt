package io.violabs.freya.controller

import io.violabs.freya.TestVariables.DATE_OF_BIRTH
import io.violabs.freya.TestVariables.JOIN_DATE
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerFunctionalTest(
    @Autowired val client: WebTestClient
) {

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
}