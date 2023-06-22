package io.violabs.freya.controller

import io.violabs.freya.DatabaseTestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@Import(DatabaseTestConfig::class)
class LibraryControllerFunctionalTest(
    @Autowired val client: WebTestClient,
    @Autowired val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder
) {

    @Test
    fun `getLibraryDetailsByUserId will return a library for a given user id`() {
        testDatabaseSeeder.seedAll()

        client
            .get()
            .uri("/api/libraries/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.user.id").isEqualTo(1)
            .jsonPath("$.books[0].id").isEqualTo(1)
            .jsonPath("$.books[1].id").isEqualTo(2)
    }

    @Test
    fun `addBookToLibrary will add a book to a library for a given user id`() {
        testDatabaseSeeder.seedUserBook()

        client
            .post()
            .uri("/api/libraries/1/book/3")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "userId": 1,
                        "bookId": 3
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0]").isEqualTo(1)
            .jsonPath("$[1]").isEqualTo(2)
            .jsonPath("$[2]").isEqualTo(3)
    }
}