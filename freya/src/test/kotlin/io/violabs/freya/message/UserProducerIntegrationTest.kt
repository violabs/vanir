package io.violabs.freya.message

import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

@SpringBootTest
class UserProducerIntegrationTest(
    @Autowired private val userProducer: UserProducer,
    @Autowired private val kafkaConsumer: KafkaConsumer
) {

    @Test
    fun `should send user data to kafka`() = runBlocking {
        val user = AppUser(1, "test", "test", "test", "test")
        userProducer.sendUserData(user)
        val receivedUser = kafkaConsumer.records.poll(5, TimeUnit.SECONDS)
        assert(receivedUser == user) {
            "EXPECT: $user\nACTUAL: $receivedUser"
        }
    }
}

@Component
class KafkaConsumer {
    val records = LinkedBlockingDeque<AppUser>()
    @KafkaListener(topics = ["user"], groupId = "json")
    fun consume(user: AppUser) {
        println("Received user: $user")
        records.add(user)
    }
}