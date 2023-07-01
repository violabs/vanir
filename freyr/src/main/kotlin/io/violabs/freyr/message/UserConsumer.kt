package io.violabs.freyr.message

import io.violabs.core.domain.UserMessage
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Component

@Component
class UserConsumer(
    private val userConsumerTemplate: ReactiveKafkaConsumerTemplate<String, UserMessage>,
) {
}