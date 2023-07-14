package io.violabs.freya

import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.kafka.receiver.ReceiverOptions

object FreyaTestUtils {
    fun <T> buildKafkaConsumer(configs: Map<String, Any>, topic: String): TestKafkaConsumer<T> =
        configs
            .let { ReceiverOptions.create<String, T>(it) }
            .subscription(listOf(topic))
            .let { ReactiveKafkaConsumerTemplate(it) }
            .let { TestKafkaConsumer(it) }
}