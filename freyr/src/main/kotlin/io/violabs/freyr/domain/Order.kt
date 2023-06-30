package io.violabs.freyr.domain

data class Order(
    val id: String,
    val accountId: Long,
    val bookId: Long,
    val orderDate: String // was an Instant, but I couldn't get it to work :(
)