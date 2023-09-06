package io.violabs.freyr.controller

import io.violabs.core.TestUtils
import io.violabs.core.domain.OrderMessage
import io.violabs.freyr.FreyrTestUtils
import io.violabs.freyr.KafkaTestConfig
import io.violabs.freyr.TestKafkaConsumer
import io.violabs.freyr.TestOrderKafkaProperties
import io.violabs.freyr.config.AppKafkaProperties
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
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
@SpringBootTest
class OrderControllerFunctionalTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val appKafkaProperties: AppKafkaProperties,
    @Autowired private val adminClient: AdminClient,
    @Autowired private val testOrderKafkaProperties: TestOrderKafkaProperties
) {
    val uuid = UUID.nameUUIDFromBytes(1.toString().toByteArray()).toString()

    @Test
    fun `createOrder will create an order`() = setupKafka(
        "order-controller-fn-test-create",
        OrderMessage("$uuid:1", 1, 1)
    ) {
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
            .jsonPath("$.id").isEqualTo("$uuid:1")
            .jsonPath("$.accountId").isEqualTo(uuid)
            .jsonPath("$.bookId").isEqualTo(1)
    }

    private fun setupKafka(
        topic: String,
        expectedMessage: OrderMessage,
        retentionMs: Int = 500,
        testFn: () -> Unit
    ): Unit = runBlocking {
        val kafkaConsumer: TestKafkaConsumer<OrderMessage> = FreyrTestUtils.buildKafkaConsumer(
            testOrderKafkaProperties.properties,
            topic
        )

        appKafkaProperties.orderTopic = topic

        val newTopic: NewTopic = appKafkaProperties.newOrderTopic()

        newTopic.configs(mapOf("retention.ms" to retentionMs.toString()))

        adminClient.createTopics(listOf(appKafkaProperties.newOrderTopic()))

        testFn()

        val receivedMessage = withTimeoutOrNull(10_000) { kafkaConsumer.consume() }
        adminClient.deleteTopics(listOf(topic))
        TestUtils.assertEquals(expectedMessage, receivedMessage)
    }
}