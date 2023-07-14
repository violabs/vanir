package io.violabs.freyr.message

import io.violabs.core.TestUtils
import io.violabs.core.domain.OrderMessage
import io.violabs.freyr.FreyrTestUtils
import io.violabs.freyr.KafkaTestConfig
import io.violabs.freyr.TestKafkaConsumer
import io.violabs.freyr.TestOrderKafkaProperties
import io.violabs.freyr.config.AppKafkaProperties
import io.violabs.freyr.domain.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.kafka.clients.admin.AdminClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(KafkaTestConfig::class)
class OrderProducerIntegrationTest(
    @Autowired private val orderProducer: OrderProducer,
    @Autowired private val appKafkaProperties: AppKafkaProperties,
    @Autowired private val adminClient: AdminClient,
    @Autowired private val testOrderKafkaProps: TestOrderKafkaProperties
) {
    @Test
    fun `should send order data to kafka`(): Unit = runBlocking {
        val tempTopic = "order-producer-int-test"

        val kafkaConsumer: TestKafkaConsumer<OrderMessage> = FreyrTestUtils.buildKafkaConsumer(
            testOrderKafkaProps.properties,
            tempTopic
        )

        appKafkaProperties.orderTopic = tempTopic

        adminClient.createTopics(listOf(appKafkaProperties.newOrderTopic()))

        val order = Order("1", "1", 1)
        val expected = OrderMessage("1", 1, 1)

        orderProducer.sendOrderMessage(1, order)
        delay(5000)
        val receivedMessage = withTimeoutOrNull(10_000) { kafkaConsumer.consume() }
        adminClient.deleteTopics(listOf(appKafkaProperties.orderTopic))
        TestUtils.assertEquals(expected, receivedMessage)
    }
}