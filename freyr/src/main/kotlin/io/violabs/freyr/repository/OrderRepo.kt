package io.violabs.freyr.repository

import io.violabs.freyr.domain.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.stereotype.Service

@Service
class OrderRepo(@Autowired factory: ReactiveRedisConnectionFactory) :
    RedisRepo<Order>(factory, Order::class.java, "order") {
    suspend fun save(order: Order): Boolean = super.save(order, order.id)
}