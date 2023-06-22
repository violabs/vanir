package io.violabs.freya

import io.violabs.core.domain.OrderMessage
import io.violabs.core.domain.UserMessage
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderResult
import java.util.function.Supplier

typealias OrderProducerTemplate = ReactiveKafkaProducerTemplate<String, OrderMessage>
typealias UserConsumerTemplate = ReactiveKafkaConsumerTemplate<String, UserMessage>
typealias UserListenerFactory = ConcurrentKafkaListenerContainerFactory<String, UserMessage>

@TestConfiguration
open class KafkaTestConfig {
    @Value("\${spring.kafka.bootstrap-servers:localhost:29092,localhost:39092}")
    lateinit var bootstrapServers: String

    @Value("\${app.kafka.user-topic:users}")
    private lateinit var userTopicString: String

    open fun userConsumerConfigs(): Map<String, Any> {
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
    }

    @Bean
    open fun orderProducerTemplate(producerConfigs: Map<String, Any>): OrderProducerTemplate {
        return producerConfigs
            .let { SenderOptions.create<String, OrderMessage>(it) }
            .let { ReactiveKafkaProducerTemplate(it) }
    }

    @Bean
    open fun orderProducer(orderProducerTemplate: OrderProducerTemplate): OrderKafkaProducer {
        return OrderKafkaProducer(orderProducerTemplate)
    }

    @Bean
    open fun userConsumerTemplate(): UserConsumerTemplate {
        return userConsumerConfigs()
            .let { ReceiverOptions.create<String, UserMessage>(it).subscription(listOf(userTopicString)) }
            .let { ReactiveKafkaConsumerTemplate(it) }
    }

    @Bean
    open fun userKafkaConsumer(userConsumerTemplate: UserConsumerTemplate): UserKafkaConsumer {
        return UserKafkaConsumer(userConsumerTemplate)
    }

    @Bean
    open fun userKafkaListenerContainerFactory(producerConfigs: Map<String, Any>): UserListenerFactory {
        val jsonDeserializer = JsonDeserializer<UserMessage>()
        jsonDeserializer.addTrustedPackages("io.violabs.core.domain")

        val keySupplier: Supplier<Deserializer<String>> = Supplier { StringDeserializer() }
        val valueSupplier: Supplier<Deserializer<UserMessage>> = Supplier { jsonDeserializer }

        val consumerFactory = DefaultKafkaConsumerFactory(userConsumerConfigs(), keySupplier, valueSupplier)
        val producerFactory = DefaultKafkaProducerFactory<String, Any>(producerConfigs)
        val kafkaTemplate = KafkaTemplate(producerFactory)
        val deadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer)

        return ConcurrentKafkaListenerContainerFactory<String, UserMessage>()
            .also { it.consumerFactory = consumerFactory }
            .also { it.setCommonErrorHandler(errorHandler) }
    }

    class UserKafkaConsumer(private val consumerTemplate: UserConsumerTemplate) {
        suspend fun consume(): UserMessage? {
            return consumerTemplate
                .receive()
                .awaitFirstOrNull()
                ?.value()
                ?.also { println("Received user message: $it") }
                ?: run { println("No user received"); null }
        }
    }

    class OrderKafkaProducer(private val producerTemplate: OrderProducerTemplate) {
        suspend fun sendOrderData(topic: String, order: OrderMessage): SenderResult<Void>? {
            return producerTemplate
                .send(topic, order.orderId, order)
                .doOnEach { println("Sent order data: $order") }
                .awaitFirstOrNull()
        }
    }
}