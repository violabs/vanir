package io.violabs.freyr

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

typealias UserProducerTemplate = ReactiveKafkaProducerTemplate<String, UserMessage>
typealias OrderConsumerTemplate = ReactiveKafkaConsumerTemplate<String, OrderMessage>
typealias OrderListenerFactory = ConcurrentKafkaListenerContainerFactory<String, OrderMessage>

@TestConfiguration
open class KafkaTestConfig {
    @Value("\${spring.kafka.bootstrap-servers:localhost:29092,localhost:39092}")
    lateinit var bootstrapServers: String

    @Value("\${app.kafka.user-topic:orders}")
    private lateinit var ordersTopicString: String

    open fun orderConsumerConfigs(): Map<String, Any> {
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
    open fun userProducerTemplate(producerConfigs: Map<String, Any>): UserProducerTemplate {
        return producerConfigs
            .let { SenderOptions.create<String, UserMessage>(it) }
            .let { ReactiveKafkaProducerTemplate(it) }
    }

    @Bean
    open fun userProducer(orderProducerTemplate: UserProducerTemplate): UserProducer {
        return UserProducer(orderProducerTemplate)
    }

    @Bean
    open fun orderConsumerTemplate(): OrderConsumerTemplate {
        return orderConsumerConfigs()
            .let { ReceiverOptions.create<String, OrderMessage>(it).subscription(listOf(ordersTopicString)) }
            .let { ReactiveKafkaConsumerTemplate(it) }
    }

    @Bean
    open fun orderConsumer(userConsumerTemplate: OrderConsumerTemplate): OrderConsumer {
        return OrderConsumer(userConsumerTemplate)
    }

    @Bean
    open fun orderKafkaListenerContainerFactory(producerConfigs: Map<String, Any>): OrderListenerFactory {
        val jsonDeserializer = JsonDeserializer<OrderMessage>()
        jsonDeserializer.addTrustedPackages("io.violabs.core.domain")

        val keySupplier: Supplier<Deserializer<String>> = Supplier { StringDeserializer() }
        val valueSupplier: Supplier<Deserializer<OrderMessage>> = Supplier { jsonDeserializer }

        val consumerFactory = DefaultKafkaConsumerFactory(orderConsumerConfigs(), keySupplier, valueSupplier)
        val producerFactory = DefaultKafkaProducerFactory<String, Any>(producerConfigs)
        val kafkaTemplate = KafkaTemplate(producerFactory)
        val deadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer)

        return ConcurrentKafkaListenerContainerFactory<String, OrderMessage>()
            .also { it.consumerFactory = consumerFactory }
            .also { it.setCommonErrorHandler(errorHandler) }
    }

    class OrderConsumer(private val consumerTemplate: OrderConsumerTemplate) {
        suspend fun consume(): OrderMessage? {
            return consumerTemplate
                .receive()
                .awaitFirstOrNull()
                ?.value()
                ?.also { println("Received user message: $it") }
                ?: run { println("No user received"); null }
        }
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