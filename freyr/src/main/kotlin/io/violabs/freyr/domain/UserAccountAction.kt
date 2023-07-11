package io.violabs.freyr.domain

import io.violabs.core.domain.UserMessage

data class UserAccountAction(
    var userMessage: UserMessage,
    var account: Account? = null,
    var accountId: String? = null,
    var user: AppUser? = null,
    var saved: Boolean? = null,
    var deleted: Boolean? = null
) {
    val userId: Long = userMessage.userId
    fun accountIdNotNull(): String = accountId ?: throw Exception("Account id is null")
}