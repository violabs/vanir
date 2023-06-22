package io.violabs.freya.message

import io.violabs.core.domain.OrderMessage
import io.violabs.freya.service.LibraryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service

@Service
class OrderConsumer(
    private val orderConsumerTemplate: ReactiveKafkaConsumerTemplate<String, OrderMessage>,
    private val libraryService: LibraryService
) {
    suspend fun consume(): Flow<Long> {
        return orderConsumerTemplate
            .receive()
            .awaitFirstOrNull()
            ?.value()
            ?.let { libraryService.addBookToLibrary(it.userId, it.bookId) }
            ?: emptyFlow()
    }
}