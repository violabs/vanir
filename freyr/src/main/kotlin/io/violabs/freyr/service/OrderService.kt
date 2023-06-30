package io.violabs.freyr.service

import io.violabs.freyr.domain.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRedisOps: ReactiveRedisOperations<String, Order>)  {

    suspend fun saveOrder(order: Order): Boolean {
        val id: String = order.id ?: throw Exception("Missing Id!!")

        return orderRedisOps.opsForValue().set(id, order).awaitSingleOrNull() ?: false
    }

    suspend fun findOrderById(id: String): Order? {
        return orderRedisOps.opsForValue().get(id).awaitSingleOrNull()
    }

    suspend fun deleteOrderById(id: String): Boolean {
        return orderRedisOps.opsForValue().delete(id).awaitSingle() ?: false
    }

    fun findAllOrders(): Flow<Order> {
        return orderRedisOps.keys("*").flatMap { orderRedisOps.opsForValue().get(it) }.asFlow()
    }
}