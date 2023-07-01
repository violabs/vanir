package io.violabs.freyr.client

import io.violabs.core.domain.UserMessage
import io.violabs.freyr.domain.AppUser
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FreyaUserClient(private val webClient: WebClient) {

    suspend fun fetchUser(userMessage: UserMessage): AppUser? =
        webClient.get()
            .uri(userMessage.uri)
            .retrieve()
            .bodyToMono(AppUser::class.java)
            .awaitSingleOrNull()
}