//package io.violabs.freyr.config
//
//import io.violabs.freyr.domain.Account
//import io.violabs.freyr.domain.Order
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory as ConnectionFactory
//import org.springframework.data.redis.core.ReactiveRedisOperations
//import org.springframework.data.redis.core.ReactiveRedisTemplate
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
//import org.springframework.data.redis.serializer.RedisSerializationContext
//import org.springframework.data.redis.serializer.StringRedisSerializer
//
//typealias OrderRedisOps = ReactiveRedisOperations<String, Order>
//typealias AccountRedisOps = ReactiveRedisOperations<String, Account>
//typealias RedisOps<T> = ReactiveRedisOperations<String, T>
//
//@Configuration
//open class RedisConfig {
//    @Bean
//    open fun orderRedisOps(factory: ConnectionFactory): OrderRedisOps = createRedisOps(factory)
//
//    @Bean
//    open fun accountRedisOps(factory: ConnectionFactory): AccountRedisOps = createRedisOps(factory)
//
//    private inline fun <reified T> createRedisOps(factory: ConnectionFactory): RedisOps<T> {
//        val serializer = Jackson2JsonRedisSerializer(T::class.java)
//
//        val context = RedisSerializationContext
//            .newSerializationContext<String, T>(StringRedisSerializer())
//            .value(serializer)
//            .build()
//
//        return ReactiveRedisTemplate(factory, context)
//    }
//}