package io.violabs.freya.message

import io.violabs.core.TestUtils
import io.violabs.core.domain.OrderMessage
import io.violabs.freya.DatabaseTestConfig
import io.violabs.freya.KafkaTestConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootTest(properties = ["app.kafka.order-topic=order-test-1"])
@Import(KafkaTestConfig::class, DatabaseTestConfig::class, OrderConsumerFunctionalTest.TopicConfig::class)
class OrderConsumerFunctionalTest(
    @Autowired private val orderConsumer: OrderConsumer,
    @Autowired private val orderKafkaProducer: KafkaTestConfig.OrderKafkaProducer,
    @Autowired private val testDatabaseSeeder: DatabaseTestConfig.TestDatabaseSeeder,
    @Autowired private val adminClient: AdminClient,
    @Autowired private val orderTopic1: NewTopic
) {

    @BeforeEach
    fun setup() {
        adminClient.deleteTopics(listOf("order-test-1"))
        adminClient.createTopics(listOf(orderTopic1))
    }

    @Test
    fun `should consume order data from kafka`() = runBlocking {
        val topic = "order-test-1"
        testDatabaseSeeder.seedUserBook()
        val orderMessage = OrderMessage("1", 1, 3)
        orderKafkaProducer.sendOrderData(topic, orderMessage)
        delay(5000)
        val ids: List<Long> = withTimeoutOrNull(30_000) { orderConsumer.consume() }!!.toList()
        TestUtils.assertEquals(listOf(1L, 2L, 3L), ids)
    }

    @TestConfiguration
    open class TopicConfig {
        @Bean
        open fun orderTopic1(): NewTopic = NewTopic("order-test-1", 1, 1.toShort())
    }
}