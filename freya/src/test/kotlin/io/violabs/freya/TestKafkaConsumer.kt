package io.violabs.freya

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate

class TestKafkaConsumer<T>(private val template: ReactiveKafkaConsumerTemplate<String, T>) {

    suspend fun consume(): T? {
        return template
            .receive()
            .awaitFirstOrNull()
            ?.value()
            ?.also { println("Received message: $it") }
            ?: run { println("No message received"); null }
    }
}