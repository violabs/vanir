package io.violabs.freyr.config

import io.violabs.freyr.domain.Order
import jakarta.annotation.PostConstruct
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component

@Component
class RedisDataLoader(
    private val factory: ReactiveRedisConnectionFactory,
    private val orderRedisOps: ReactiveRedisOperations<String, Order>
) {

    @PostConstruct
    fun loadData() {

    }
}