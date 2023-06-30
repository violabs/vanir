package io.violabs.freyr.service

import io.violabs.freyr.config.OrderRedisOps
import io.violabs.freyr.domain.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.Instant
import java.util.*

@SpringBootTest
@Import(RedisReactiveAutoConfiguration::class)
class OrderServiceIntegrationTest(
    @Autowired private val orderService: OrderService,
    @Autowired private val orderRedisOps: OrderRedisOps
) {

    @Test
    fun `saveOrder saves order to redis`() = runBlocking {
        //given
        val keys: Flow<String> = orderRedisOps.keys("*").asFlow()
        keys.map { orderRedisOps.delete(it).awaitSingleOrNull() }.toList().forEach(::println)

        val now = Instant.now()
        val uuid = UUID.nameUUIDFromBytes("test".toByteArray()).toString()
        val expected = Order(uuid, 1, 1, now.toString())

        //when
        val actual = orderService.saveOrder(expected)

        //then
        assert(actual) {
            "Was not able to save $expected"
        }
    }
}