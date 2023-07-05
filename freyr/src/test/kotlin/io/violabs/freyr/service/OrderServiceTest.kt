package io.violabs.freyr.service

import io.mockk.*
import io.violabs.core.TestUtils
import io.violabs.freyr.config.UuidGenerator
import io.violabs.freyr.domain.OrderDetails
import io.violabs.freyr.message.OrderProducer
import io.violabs.freyr.repository.OrderRepo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class OrderServiceTest {
    private val uuidGenerator: UuidGenerator = mockk()
    private val orderProducer: OrderProducer = mockk()
    private val orderRepo: OrderRepo = mockk()

    private val orderService = OrderService(uuidGenerator, orderProducer, orderRepo)

    @AfterEach
    fun teardown() {
        confirmVerified(uuidGenerator, orderProducer, orderRepo)
    }

    @Test
    fun `createOrder will send a message if it saved`() = runBlocking {
        //given
        val orderDetails = OrderDetails(1, 2)
        val accountId = "123"

        every { uuidGenerator.generateString(orderDetails.userId) } returns accountId
        coEvery { orderRepo.save(any()) } returns true
        coEvery { orderProducer.sendOrderMessage(any(), any()) } returns mockk()

        //when
        val order = orderService.createOrder(orderDetails)

        //then
        verify { uuidGenerator.generateString(orderDetails.userId) }
        coVerify { orderRepo.save(any()) }
        coVerify { orderProducer.sendOrderMessage(orderDetails.userId, order) }
        TestUtils.assertEquals(order.id, "$accountId-${orderDetails.bookId}")
    }
}