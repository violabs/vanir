package io.violabs.freyr.domain

data class AppUser(
    val id: Long? = null,
    val username: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String? = null
)