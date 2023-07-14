package io.violabs.freyr

import io.violabs.core.domain.UserMessage
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderResult

typealias UserProducerTemplate = ReactiveKafkaProducerTemplate<String, UserMessage>

class TestOrderKafkaProperties(val properties: Map<String, Any>)

@TestConfiguration
open class KafkaTestConfig {
    @Value("\${spring.kafka.bootstrap-servers:localhost:29092,localhost:39092}")
    lateinit var bootstrapServers: String

    @Bean
    open fun testOrderKafkaProperties(): TestOrderKafkaProperties {
        return KafkaProperties()
            .also {
                val consumer = it.consumer

                consumer.bootstrapServers = bootstrapServers.split(",").toList()
                consumer.keyDeserializer = StringDeserializer::class.java
                consumer.valueDeserializer = JsonDeserializer::class.java
                consumer.groupId = "freya-test-user"
                consumer.autoOffsetReset = "earliest"
                consumer.properties["spring.json.trusted.packages"] = "io.violabs.core.domain"
            }
            .buildConsumerProperties()
            .let(::TestOrderKafkaProperties)
    }

    @Bean
    open fun userProducerTemplate(producerConfigs: Map<String, Any>): UserProducerTemplate {
        return producerConfigs
            .let { SenderOptions.create<String, UserMessage>(it) }
            .let { ReactiveKafkaProducerTemplate(it) }
    }

    @Bean
    open fun userProducer(orderProducerTemplate: UserProducerTemplate): UserProducer {
        return UserProducer(orderProducerTemplate)
    }

    class UserProducer(private val producerTemplate: UserProducerTemplate) {
        suspend fun send(topic: String, order: UserMessage): SenderResult<Void>? {
            return producerTemplate
                .send(topic, order.userId.toString(), order)
                .doOnEach { println("Sent order data: $order") }
                .awaitFirstOrNull()
        }
    }
}