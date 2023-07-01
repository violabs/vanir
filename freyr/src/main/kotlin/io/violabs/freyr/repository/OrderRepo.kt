package io.violabs.freyr.repository

import io.violabs.freyr.domain.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.stereotype.Service

@Service
class OrderRepo(@Autowired factory: ReactiveRedisConnectionFactory) : RedisRepo<Order>(factory, Order::class.java) {

    suspend fun saveOrder(order: Order): Boolean {
        val id: String = order.id ?: throw Exception("Missing Id!!")

        return operations.opsForValue().set(id, order).awaitSingleOrNull() ?: false
    }

    suspend fun findOrderById(id: String): Order? {
        return operations.opsForValue().get(id).awaitSingleOrNull()
    }

    suspend fun deleteOrderById(id: String): Boolean {
        return operations.opsForValue().delete(id).awaitSingle() ?: false
    }

    fun findAllOrders(): Flow<Order> {
        return operations.keys("*").flatMap { operations.opsForValue().get(it) }.asFlow()
    }
}