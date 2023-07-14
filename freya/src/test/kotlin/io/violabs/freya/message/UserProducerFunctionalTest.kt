package io.violabs.freya.message

import io.violabs.core.TestUtils
import io.violabs.core.domain.UserMessage
import io.violabs.freya.FreyaTestUtils
import io.violabs.freya.KafkaTestConfig
import io.violabs.freya.TestKafkaConsumer
import io.violabs.freya.TestUserKafkaProperties
import io.violabs.freya.config.AppKafkaProperties
import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.kafka.clients.admin.AdminClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(KafkaTestConfig::class)
class UserProducerFunctionalTest(
    @Autowired private val userProducer: UserProducer,
    @Autowired private val appKafkaProperties: AppKafkaProperties,
    @Autowired private val adminClient: AdminClient,
    @Autowired private val testUserKafkaProps: TestUserKafkaProperties
) {

    @Test
    fun `should send user data to kafka`(): Unit = runBlocking {
        val tempTopic = "user-producer-int-test"

        val kafkaConsumer: TestKafkaConsumer<UserMessage> = FreyaTestUtils.buildKafkaConsumer(
            testUserKafkaProps.properties,
            tempTopic
        )

        appKafkaProperties.userTopic = tempTopic

        adminClient.createTopics(listOf(appKafkaProperties.newUserTopic()))
        val user = AppUser(1, "test", "test", "test", "test")
        val message = UserMessage(1, "http://localhost:8080/api/users/1", UserMessage.Type.USER_CREATED)
        userProducer.sendUserData(user, UserMessage.Type.USER_CREATED)
        val receivedMessage = withTimeoutOrNull(10_000) { kafkaConsumer.consume() }
        adminClient.deleteTopics(listOf(appKafkaProperties.userTopic))
        TestUtils.assertEquals(message, receivedMessage)
    }
}