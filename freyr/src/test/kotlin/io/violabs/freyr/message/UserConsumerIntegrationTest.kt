package io.violabs.freyr.message

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.violabs.core.domain.UserMessage
import io.violabs.freyr.KafkaTestConfig
import io.violabs.freyr.domain.UserAccountAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.kafka.clients.admin.NewTopic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootTest(properties = ["app.kafka.user-topic=user-test-1"])
@Import(KafkaTestConfig::class, UserConsumerIntegrationTest.TopicConfig::class)
class UserConsumerIntegrationTest(
    @Autowired private val userConsumer: UserConsumer,
    @Autowired private val userProducer: KafkaTestConfig.UserProducer
) {
    @MockkBean
    private lateinit var userHandler: UserHandler

    @Test
    fun `should consume user data from kafka`() = runBlocking {
        //given
        val userMessage = UserMessage(1, "test", UserMessage.Type.USER_CREATED)
        val userAccountAction = UserAccountAction(userMessage, saved = true)

        coEvery { userHandler.handleUserMessage(userMessage) } returns userAccountAction

        //when
        userProducer.send(TOPIC, userMessage)

        //then
        delay(5000)
        val action = withTimeoutOrNull(30_000) { userConsumer.consume() }
        assert(action != null)
        assert(action!!.saved!!)
    }


    @TestConfiguration
    open class TopicConfig {
        @Bean
        open fun userTopic1(): NewTopic = NewTopic(TOPIC, 1, 1.toShort())
    }

    companion object {
        const val TOPIC = "user-test-1"
    }
}