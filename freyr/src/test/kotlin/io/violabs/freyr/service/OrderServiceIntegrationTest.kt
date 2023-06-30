package io.violabs.freyr.service

import io.violabs.core.TestUtils
import io.violabs.freyr.config.OrderRedisOps
import io.violabs.freyr.domain.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    private val now = Instant.now()
    private val sharedUuid = UUID.nameUUIDFromBytes("test".toByteArray()).toString()
    private val sharedOrder = Order(sharedUuid, 1, 1, now.toString())

    @BeforeEach
    fun setup() = runBlocking {
        val keys: Flow<String> = orderRedisOps.keys("*").asFlow()
        keys.map { orderRedisOps.delete(it).awaitSingleOrNull() }.toList().forEach(::println)
    }

    @Test
    fun `saveOrder throws exception if id is null`(): Unit = runBlocking {
        assertThrows<Exception> { orderService.saveOrder(Order()) }
    }

    @Test
    fun `saveOrder saves order to redis`() = runBlocking {
        //when
        val actual = orderService.saveOrder(sharedOrder)

        //then
        assert(actual) {
            "Was not able to save $sharedOrder"
        }
    }

    @Test
    fun `findOrderById will return null if not found`() = runBlocking {
        //when
        val actual: Order? = orderService.findOrderById(sharedUuid)

        //then
        TestUtils.assertEquals(null, actual)
    }

    @Test
    fun `findOrderById will find an order when it exists`() = runBlocking {
        //given
        saveRawOrder()

        //when
        val actual: Order? = orderService.findOrderById(sharedUuid)

        //then
        TestUtils.assertEquals(sharedOrder, actual)
    }

    @Test
    fun `deleteOrderById will return false when order does not exist`() = runBlocking {
        //when
        val actual: Boolean = orderService.deleteOrderById(sharedUuid)

        //then
        assert(!actual) {
            "Was able to delete $sharedOrder"
        }
    }

    @Test
    fun `deleteOrderById will delete an order when it exists`() = runBlocking {
        //given
        saveRawOrder()

        //when
        val actual: Boolean = orderService.deleteOrderById(sharedUuid)

        //then
        assert(actual) {
            "Was not able to delete $sharedOrder"
        }
    }

    @Test
    fun `findAllOrders will return empty list when no orders exist`() = runBlocking {
        //when
        val actual: List<Order> = orderService.findAllOrders().toList()

        //then
        assert(actual.isEmpty()) {
            "Expected empty list but got $actual"
        }
    }

    @Test
    fun `findAllOrders will return list of orders when orders exist`() = runBlocking {
        //given
        saveRawOrder()
        val uuid2 = UUID.nameUUIDFromBytes("test2".toByteArray()).toString()
        val order2 = Order(uuid2, 2, 2, now.toString())
        saveRawOrder(uuid2, order2)

        //when
        val actual: List<Order> = orderService.findAllOrders().toList()

        //then
        TestUtils.assertEquals(listOf(sharedOrder, order2), actual)
    }

    private suspend fun saveRawOrder(uuid: String = sharedUuid, order: Order = sharedOrder) {
        orderRedisOps
            .opsForValue()
            .set(uuid, order)
            .awaitSingle()
    }
}