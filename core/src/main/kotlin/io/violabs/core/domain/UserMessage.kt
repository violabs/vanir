package io.violabs.core.domain

data class UserMessage(
    val userId: Long,
    val uri: String,
    val type: Type
) {
    enum class Type {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_DEACTIVATED
    }
}