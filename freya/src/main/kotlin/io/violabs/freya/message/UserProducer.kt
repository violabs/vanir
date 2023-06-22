package io.violabs.freya.message

import io.violabs.core.domain.UserMessage
import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component
import reactor.kafka.sender.SenderResult

@Component
class UserProducer(private val producerTemplate: ReactiveKafkaProducerTemplate<String, UserMessage>) {
    suspend fun sendUserData(user: AppUser): SenderResult<Void>? {
        val message = UserMessage(user.id!!, "localhost:8080/user/${user.id}")

        return producerTemplate
            .send("users", user.id.toString(), message)
            .doOnEach { println("Sent user data: $user") }
            .awaitSingleOrNull()
    }
}