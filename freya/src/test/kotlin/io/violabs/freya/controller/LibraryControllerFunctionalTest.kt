package io.violabs.freya.controller

import io.violabs.freya.DatabaseTestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
}