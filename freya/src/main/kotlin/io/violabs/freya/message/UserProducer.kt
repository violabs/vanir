package io.violabs.freya.message

import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import reactor.kafka.sender.SenderResult

private const val USER_TOPIC = "user"

@Component
class UserProducer(private val producerTemplate: ReactiveKafkaProducerTemplate<String, AppUser>) {
    suspend fun sendUserData(user: AppUser): SenderResult<Void>? {
        return producerTemplate
            .send(USER_TOPIC, user.id.toString(), user)
            .doOnEach { println("Sent user data: $user") }
            .awaitSingleOrNull()
    }
}