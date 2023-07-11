package io.violabs.freyr.message

import io.violabs.core.domain.UserMessage
import io.violabs.freyr.domain.UserAccountAction
import io.violabs.freyr.service.UserAccountService
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class UserHandler(private val userAccountService: UserAccountService) {

    suspend fun handleUserMessage(userMessage: UserMessage): UserAccountAction {
        val action = UserAccountAction(userMessage)

        return when (val type: UserMessage.Type = userMessage.type) {
            UserMessage.Type.USER_CREATED -> userAccountService.createAccount(action)
            UserMessage.Type.USER_UPDATED -> userAccountService.updateAccount(action)
            UserMessage.Type.USER_DELETED -> userAccountService.deleteAccount(action)
            else -> {
                logger.info { "No action needed for $type" }
                action
            }
        }
    }

    companion object : KLogging()
}