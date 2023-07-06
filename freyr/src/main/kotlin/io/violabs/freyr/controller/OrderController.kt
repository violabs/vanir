package io.violabs.freyr.controller

import io.violabs.freyr.domain.Order
import io.violabs.freyr.domain.OrderDetails
import io.violabs.freyr.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/orders")
class OrderController(private val orderService: OrderService) {
    @PostMapping
    suspend fun createOrder(@RequestBody orderDetails: OrderDetails): Order = orderService.createOrder(orderDetails)
}