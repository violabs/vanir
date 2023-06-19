package io.violabs.freya.message

import KafkaTestConfig
import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(KafkaTestConfig::class)
class UserProducerIntegrationTest(
    @Autowired private val userProducer: UserProducer,
    @Autowired private val userKafkaConsumer: KafkaTestConfig.KafkaConsumer
) {

    @Test
    fun `should send user data to kafka`() = runBlocking {
        val user = AppUser(1, "test", "test", "test", "test")
        userProducer.sendUserData(user)
        delay(2000)
        val receivedUser = withTimeoutOrNull(15_000) { userKafkaConsumer.consume() }
        assert(receivedUser == user) {
            "EXPECT: $user\nACTUAL: $receivedUser"
        }
    }
}