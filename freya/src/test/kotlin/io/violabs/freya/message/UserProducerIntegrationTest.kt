package io.violabs.freya.message

import io.violabs.core.TestUtils
import io.violabs.core.domain.UserMessage
import io.violabs.freya.KafkaTestConfig
import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(KafkaTestConfig::class)
class UserProducerIntegrationTest(
    @Autowired private val userProducer: UserProducer,
    @Autowired private val userKafkaConsumer: KafkaTestConfig.UserKafkaConsumer
) {

    @Test
    fun `should send user data to kafka`() = runBlocking {
        val user = AppUser(1, "test", "test", "test", "test")
        val message = UserMessage(1, "http://localhost:8080/api/users/1", UserMessage.Type.USER_CREATED)
        userProducer.sendUserData(user)
        val receivedMessage = userKafkaConsumer.consume()
        TestUtils.assertEquals(message, receivedMessage)
    }
}