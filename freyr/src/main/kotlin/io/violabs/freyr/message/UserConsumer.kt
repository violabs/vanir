package io.violabs.freyr.message

import io.violabs.freyr.config.UserConsumerTemplate
import io.violabs.freyr.domain.UserAccountAction
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class UserConsumer(
    private val userConsumerTemplate: UserConsumerTemplate,
    private val userHandler: UserHandler
) {
    // call the handler
    suspend fun consume(): UserAccountAction? {
        return userConsumerTemplate
            .receive()
            .awaitFirstOrNull()
            ?.value()
            ?.also { println("Received user message: $it") }
            ?.let { userHandler.handleUserMessage(it) }
    }
}