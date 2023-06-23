package io.violabs.freyr.domain

import java.time.Instant

data class Order(
    val id: String,
    val accountId: Long,
    val bookId: Long,
    val orderDate: Instant
)