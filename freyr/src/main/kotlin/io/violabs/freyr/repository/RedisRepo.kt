package io.violabs.freyr.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

typealias RedisOps<T> = ReactiveRedisOperations<String, T>

abstract class RedisRepo<T : Any>(factory: ReactiveRedisConnectionFactory, klass: Class<T>) {
    protected var operations: RedisOps<T> = createRedisOps(factory, klass)

    suspend fun save(item: T, id: String?): Boolean =
        operations
            .opsForValue()
            .set(id ?: throw Exception("Missing id!!"), item)
            .awaitSingleOrNull()
            ?: false

    suspend fun findById(id: String): T? =
        operations
            .opsForValue()
            .get(id)
            .awaitSingleOrNull()

    fun findAll(): Flow<T> =
        operations
            .keys("*")
            .flatMap { operations.opsForValue().get(it) }
            .asFlow()

    suspend fun deleteById(id: String): Boolean =
        operations
            .opsForValue()
            .delete(id)
            .awaitSingleOrNull()
            ?: false

    suspend fun deleteAll(): Boolean =
        operations
            .keys("*")
            .flatMap { operations.opsForValue().delete(it) }
            .reduce { a, b -> a && b }
            .awaitSingleOrNull()
            ?: false

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