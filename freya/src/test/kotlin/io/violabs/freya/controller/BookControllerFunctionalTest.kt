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
class BookControllerFunctionalTest(
    @Autowired val client: WebTestClient,
    @Autowired val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder
) {

    @Test
    fun `createBook will create a book with an id`() {
        testDatabaseSeeder.truncateBook()
        client
            .post()
            .uri("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "title": "testbook",
                        "author": "Test"
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.title").isEqualTo("testbook")
            .jsonPath("$.author").isEqualTo("Test")
    }

    @Test
    fun `getBookById will return a book for a given id`() {
        testDatabaseSeeder.seedBook()

        client
            .get()
            .uri("/api/books/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Test Book")
            .jsonPath("$.author").isEqualTo("Test Author")
    }
}