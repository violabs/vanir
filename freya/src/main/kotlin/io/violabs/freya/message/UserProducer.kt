package io.violabs.freya.message

import io.violabs.core.domain.UserMessage
import io.violabs.freya.config.AppKafkaProperties
import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KLogging
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import reactor.kafka.sender.SenderResult

@Component
class UserProducer(
    private val appKafkaProperties: AppKafkaProperties,
    private val producerTemplate: ReactiveKafkaProducerTemplate<String, UserMessage>
) {
    suspend fun sendUserData(user: AppUser, type: UserMessage.Type): SenderResult<Void>? {
        val message = UserMessage(
            userId = user.id!!,
            uri = "http://localhost:8080/api/users/${user.id}",
            type = type
        )

        return sendMessage(message)
    }

    suspend fun sendMessage(message: UserMessage): SenderResult<Void>? {
        logger.info("Sending message: $message")

        return producerTemplate
            .send(appKafkaProperties.userTopic, message.userId.toString(), message)
            .doOnEach { logger.info("Sent message: $message") }
            .awaitSingleOrNull()
    }

    companion object : KLogging()
}