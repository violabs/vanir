package io.violabs.freyr.message

import io.violabs.freyr.config.UserConsumerTemplate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserConsumer(
    private val userConsumerTemplate: UserConsumerTemplate,
    private val userHandler: UserHandler
) {
    // call the handler
    suspend fun consume() {
        userConsumerTemplate
            .receive()
            .asFlow()
            .map { it.value() }
            .onEach { logger.info("Received $it") }
            .map(userHandler::handleUserMessage)
            .onEach { logger.info("Processed $it") }
            .collect()
    }

    companion object : KLogging()
}