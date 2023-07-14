package io.violabs.freyr.message

import io.violabs.core.domain.OrderMessage
import io.violabs.freyr.config.AppKafkaProperties
import io.violabs.freyr.config.OrderProducerTemplate
import io.violabs.freyr.domain.Order
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KLogging
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderResult

@Service
class OrderProducer(
    private val producerTemplate: OrderProducerTemplate,
    private val appKafkaProperties: AppKafkaProperties
) {
    suspend fun sendOrderMessage(userId: Long, order: Order): SenderResult<Void>? {
        val orderMessage = OrderMessage(order.id!!, userId, order.bookId!!)

        return producerTemplate
            .send(appKafkaProperties.orderTopic, order.id.toString(), orderMessage)
            .doOnEach { logger.info("Sent order data: $order") }
            .awaitSingleOrNull()
    }

    companion object : KLogging()
}