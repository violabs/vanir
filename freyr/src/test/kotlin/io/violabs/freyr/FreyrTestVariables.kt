package io.violabs.freyr

import io.violabs.core.domain.UserMessage
import io.violabs.freyr.domain.Account
import io.violabs.freyr.domain.AppUser
import io.violabs.freyr.domain.UserAccountAction
import java.util.UUID

object FreyrTestVariables {
    val USER_MESSAGE = UserMessage(
        userId = 1L,
        uri = "http://localhost:8083/user/1",
        type = UserMessage.Type.USER_CREATED
    )

    val USER_MESSAGE_ACTION = UserAccountAction(
        userMessage = USER_MESSAGE
    )

    val USER = AppUser(
        id = 1L,
        username = "test",
        firstname = "test",
        lastname = "test",
        email = "test@test.com"
    )

    val ACCOUNT_UUID: UUID = UUID.nameUUIDFromBytes("test".toByteArray())

    val NEW_ACCOUNT = Account(
        id = ACCOUNT_UUID.toString(),
        userId = 1L,
        userDetails = USER
    )
}