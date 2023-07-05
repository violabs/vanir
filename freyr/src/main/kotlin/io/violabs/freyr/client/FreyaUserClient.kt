package io.violabs.freyr.client

import io.violabs.freyr.domain.AppUser
import io.violabs.freyr.domain.UserAccountAction
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

interface UserClient {
    suspend fun fetchUser(action: UserAccountAction): AppUser?
}

@Component
open class FreyaUserClient(private val webClient: WebClient) : UserClient {

    override suspend fun fetchUser(action: UserAccountAction): AppUser? =
        webClient.get()
            .uri(action.userMessage.uri)
            .retrieve()
            .bodyToMono(AppUser::class.java)
            .awaitSingleOrNull()
}