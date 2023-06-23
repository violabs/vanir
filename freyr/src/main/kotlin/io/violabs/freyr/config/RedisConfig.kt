package io.violabs.freyr.config

import io.violabs.freyr.domain.Order
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

typealias OrderRedisOps = ReactiveRedisOperations<String, Order>

@Configuration
open class RedisConfig {
    @Bean
    open fun orderRedisOps(factory: ReactiveRedisConnectionFactory): OrderRedisOps {
        val serializer = Jackson2JsonRedisSerializer(Order::class.java)

        val context = RedisSerializationContext
            .newSerializationContext<String, Order>(StringRedisSerializer())
            .value(serializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }
}