package io.violabs.freya.message

import io.violabs.core.domain.OrderMessage
import io.violabs.freya.service.LibraryService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import mu.KLogging
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers

@Service
class OrderConsumer(
    private val orderConsumerTemplate: ReactiveKafkaConsumerTemplate<String, OrderMessage>,
    private val libraryService: LibraryService
) {
    fun consume() {
        orderConsumerTemplate
            .receive()
            .subscribeOn(Schedulers.newBoundedElastic(5, 10, "order-consumer"))
            .asFlow()
            .map { it.value() }
            .onEach { logger.info("Receiving $it") }
            .onEach { libraryService.addBookToLibrary(it.userId, it.bookId) }
            .asFlux()
            .subscribe()
    }

    @PostConstruct
    fun init() {
        consume()
    }

    companion object : KLogging()
}