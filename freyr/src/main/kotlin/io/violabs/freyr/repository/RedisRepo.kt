package io.violabs.freyr.repository

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

typealias RedisOps<T> = ReactiveRedisOperations<String, T>

abstract class RedisRepo<T>(factory: ReactiveRedisConnectionFactory, klass: Class<T>) {
    protected var operations: RedisOps<T> = createRedisOps(factory, klass)

    companion object {
        fun <T> createRedisOps(factory: ReactiveRedisConnectionFactory, klass: Class<T>): RedisOps<T> {
            val serializer: Jackson2JsonRedisSerializer<T> = Jackson2JsonRedisSerializer(klass)

            val context: RedisSerializationContext<String, T> = RedisSerializationContext
                .newSerializationContext<String, T>(StringRedisSerializer())
                .value(serializer)
                .build()

            return ReactiveRedisTemplate(factory, context)
        }
    }
}