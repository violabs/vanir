package io.violabs.freyr.controller

import io.violabs.core.TestUtils
import io.violabs.core.domain.OrderMessage
import io.violabs.freyr.KafkaTestConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@AutoConfigureWebTestClient
@Import(KafkaTestConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerFunctionalTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val orderConsumer: KafkaTestConfig.OrderConsumer
) {
    @Test
    fun `createOrder will create an order`() = runBlocking {
        //given
        val uuid = UUID.nameUUIDFromBytes(1.toString().toByteArray()).toString()

        client
            .post()
            .uri("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """
                    {
                        "userId": 1,
                        "bookId": 1
                    }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("$uuid-1")
            .jsonPath("$.accountId").isEqualTo(uuid)
            .jsonPath("$.bookId").isEqualTo(1)

        val receivedMessage: OrderMessage? = orderConsumer.consume()

        val expected = OrderMessage("$uuid-1", 1, 1)

        TestUtils.assertEquals(expected, receivedMessage)
    }
}