package io.violabs.freyr.repository

import io.violabs.core.TestUtils
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
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import java.time.Instant
import java.util.*

@SpringBootTest
@Import(RedisReactiveAutoConfiguration::class)
class OrderRepoIntegrationTest(
    @Autowired private val orderRepo: OrderRepo,
    @Autowired private val factory: ReactiveRedisConnectionFactory
) {
    private val orderRedisOps: RedisOps<Order> = RedisRepo.createRedisOps(factory, Order::class.java)

    private val now = Instant.now()
    private val sharedUuid = UUID.nameUUIDFromBytes("test".toByteArray()).toString()
    private val sharedOrder = Order(sharedUuid, "1", 1, now.toString())

    @BeforeEach
    fun setup() = runBlocking {
        val keys: Flow<String> = orderRedisOps.keys("*").asFlow()
        keys.map { orderRedisOps.delete(it).awaitSingleOrNull() }.toList().forEach(::println)
    }

    @Test
    fun `save throws exception if id is null`(): Unit = runBlocking {
        assertThrows<Exception> { orderRepo.save(Order()) }
    }

    @Test
    fun `save saves order to redis`() = runBlocking {
        //when
        val actual = orderRepo.save(sharedOrder)

        //then
        assert(actual) {
            "Was not able to save $sharedOrder"
        }
    }

    @Test
    fun `findById will return null if not found`() = runBlocking {
        //when
        val actual: Order? = orderRepo.findById(sharedUuid)

        //then
        TestUtils.assertEquals(null, actual)
    }

    @Test
    fun `findById will find an order when it exists`() = runBlocking {
        //given
        createOrder()

        //when
        val actual: Order? = orderRepo.findById(sharedUuid)

        //then
        TestUtils.assertEquals(sharedOrder, actual)
    }

    @Test
    fun `deleteById will return false when order does not exist`() = runBlocking {
        //when
        val actual: Boolean = orderRepo.deleteById(sharedUuid)

        //then
        assert(!actual) {
            "Was not able to delete $sharedOrder"
        }
    }

    @Test
    fun `deleteById will delete an order when it exists`() = runBlocking {
        //given
        createOrder()

        //when
        val actual: Boolean = orderRepo.deleteById(sharedUuid)

        //then
        assert(actual) {
            "Was not able to delete $sharedOrder"
        }
    }

    @Test
    fun `findAll will return empty list when no orders exist`() = runBlocking {
        //when
        val actual: List<Order> = orderRepo.findAll().toList()

        //then
        assert(actual.isEmpty()) {
            "Expected empty list but got $actual"
        }
    }

    @Test
    fun `findAll will return list of orders when orders exist`() = runBlocking {
        //given
        createOrder()
        val uuid2 = UUID.nameUUIDFromBytes("test2".toByteArray()).toString()
        val order2 = Order(uuid2, "2", 2, now.toString())
        createOrder(uuid2, order2)

        //when
        val actual: List<Order> = orderRepo.findAll().toList()

        //then
        TestUtils.assertContains(actual, listOf(sharedOrder, order2))
    }

    private suspend fun createOrder(uuid: String = sharedUuid, order: Order = sharedOrder) {
        orderRedisOps
            .opsForValue()
            .set(uuid, order)
            .awaitSingle()
    }
}