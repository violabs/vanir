package io.violabs.freyr.message

import io.violabs.core.domain.UserMessage
import io.violabs.freyr.repository.AccountRepo
import org.springframework.stereotype.Component

@Component
class UserMessageHandler(private val accountRepo: AccountRepo) {

    fun handle(message: UserMessage) {
        if (message.type == UserMessage.Type.USER_UPDATED) return


    }
}