package io.violabs.freyr.domain
data class Account(
    val id: String? = null,
    val userId: Long? = null,
    val orderIds: List<String>? = null,
    val userDetails: AppUser? = null
)