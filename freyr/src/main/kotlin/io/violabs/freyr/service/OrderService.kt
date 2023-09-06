package io.violabs.freyr.service

import io.violabs.freyr.config.UuidGenerator
import io.violabs.freyr.domain.Order
import io.violabs.freyr.domain.OrderDetails
import io.violabs.freyr.message.OrderProducer
import io.violabs.freyr.repository.OrderRepo
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val uuidGenerator: UuidGenerator,
    private val orderProducer: OrderProducer,
    private val orderRepo: OrderRepo
) {
    suspend fun createOrder(orderDetails: OrderDetails): Order {
        val accountId: String = uuidGenerator.generateString(orderDetails.userId)
        val bookId: Long = orderDetails.bookId

        val order = Order(
            id = "$accountId:$bookId",
            accountId = accountId,
            bookId = bookId
        )

        val saved: Boolean = orderRepo.save(order)

        if (saved) {
            orderProducer.sendOrderMessage(orderDetails.userId, order)
        } else {
            throw Exception("Failed to save order")
        }

        return order
    }
}