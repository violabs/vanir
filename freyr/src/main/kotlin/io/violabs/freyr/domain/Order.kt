package io.violabs.freyr.domain

data class Order(
    var id: String? = null,
    val accountId: String? = null,
    val bookId: Long? = null,
    val orderDate: String? = null // was an Instant, but I couldn't get it to work :(
)