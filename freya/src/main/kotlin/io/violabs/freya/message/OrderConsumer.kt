package io.violabs.freya.message

import io.violabs.core.domain.OrderMessage
import io.violabs.freya.service.LibraryService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import mu.KLogging
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service

@Service
class OrderConsumer(
    private val orderConsumerTemplate: ReactiveKafkaConsumerTemplate<String, OrderMessage>,
    private val libraryService: LibraryService
) {
    suspend fun consume() {
        orderConsumerTemplate
            .receive()
            .asFlow()
            .map { it.value() }
            .onEach { logger.info("Receiving $it") }
            .onEach { libraryService.addBookToLibrary(it.userId, it.bookId) }
            .collect()
    }

    companion object : KLogging()
}