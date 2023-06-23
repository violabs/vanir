package io.violabes.freyr.service

import io.violabs.core.TestUtils
import io.violabs.freyr.config.OrderRedisOps
import io.violabs.freyr.config.RedisConfig
import io.violabs.freyr.domain.Order
import io.violabs.freyr.service.OrderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.Instant
import java.util.UUID

@SpringBootTest
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
        val expected = Order(uuid, 1, 1, now)

        //when
        val actual = orderService.saveOrder(expected)

        //then
        TestUtils.assertEquals(expected, actual)
    }

    private fun getKeys(): List<String> {
        return orderRedisOps.keys("*").collectList().block()!!
    }
}