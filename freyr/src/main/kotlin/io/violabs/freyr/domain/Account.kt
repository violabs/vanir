package io.violabs.freyr.domain
data class Account(
    val id: String? = null,
    val userId: Long? = null,
    var orderIds: MutableList<String>? = null,
    val userDetails: AppUser? = null
) {
    fun addOrderId(orderId: String) {
        if (orderIds == null) orderIds = mutableListOf()
        orderIds?.add(orderId)
    }
}