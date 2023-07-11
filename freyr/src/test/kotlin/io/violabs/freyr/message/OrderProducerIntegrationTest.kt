package io.violabs.freyr.message

import io.violabs.core.domain.OrderMessage
import io.violabs.freyr.KafkaTestConfig
import io.violabs.freyr.domain.Order
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(KafkaTestConfig::class)
class OrderProducerIntegrationTest(
    @Autowired private val orderProducer: OrderProducer,
    @Autowired private val orderKafkaConsumer: KafkaTestConfig.OrderConsumer
) {
    @Test
    fun `should send order data to kafka`() = runBlocking {
        val order = Order("1", "1", 1)
        val expected = OrderMessage("1", 1, 1)

        orderProducer.sendOrderMessage(1, order)
        val receivedMessage = orderKafkaConsumer.consume()
        assert(receivedMessage == expected)
    }
}