package io.violabs.freyr.message

import io.violabs.freyr.config.UserConsumerTemplate
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import mu.KLogging
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers

@Service
class UserConsumer(
    private val userConsumerTemplate: UserConsumerTemplate,
    private val userHandler: UserHandler
) {
    // call the handler
    fun consume() {
        userConsumerTemplate
            .receive()
            .subscribeOn(Schedulers.newBoundedElastic(5, 10, "user-consumer"))
            .asFlow()
            .map { it.value() }
            .onEach { logger.info("Received $it") }
            .map(userHandler::handleUserMessage)
            .onEach { logger.info("Processed $it") }
            .asFlux()
            .subscribe()
    }

    @PostConstruct
    fun init() {
        consume()
    }

    companion object : KLogging()
}