package io.violabs.core.domain

data class OrderMessage(
    val orderId: String,
    val userId: Long,
    val bookId: Long,
)