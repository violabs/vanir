package io.violabs.freyr.config

import io.violabs.core.domain.OrderMessage
import io.violabs.core.domain.UserMessage
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions
import java.util.function.Supplier

@Configuration
open class KafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers:localhost:29092,localhost:39092}")
    private lateinit var bootstrapServers: String

    @Value("\${app.kafka.user-topic:users}")
    private lateinit var userTopicString: String

    @Value("\${app.kafka.order-topic:orders}")
    private lateinit var orderTopicString: String

    @Bean
    open fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        return KafkaAdmin(configs)
    }

    @Bean
    open fun adminClient(kafkaAdmin: KafkaAdmin): AdminClient = AdminClient.create(kafkaAdmin.configurationProperties)

    @Bean open fun userTopic(): NewTopic = NewTopic(userTopicString, 1, 1.toShort())
    @Bean open fun orderTopic(): NewTopic = NewTopic(orderTopicString, 1, 1.toShort())

    @Bean
    open fun producerConfigs(): Map<String, Any> {
        return KafkaProperties()
            .also {
                val producer = it.producer

                producer.bootstrapServers = bootstrapServers.split(",").toList()
                producer.keySerializer = StringSerializer::class.java
                producer.valueSerializer = JsonSerializer::class.java
                producer.acks = "all"
            }
            .buildProducerProperties()
    }

    @Bean
    open fun userConsumerConfigs(): Map<String, Any> {
        return KafkaProperties()
            .also {
                val consumer = it.consumer

                consumer.bootstrapServers = bootstrapServers.split(",").toList()
                consumer.keyDeserializer = StringDeserializer::class.java
                consumer.valueDeserializer = JsonDeserializer::class.java
                consumer.groupId = "freya-user-consumer"
                consumer.autoOffsetReset = "earliest"
                consumer.properties["spring.json.trusted.packages"] = "io.violabs.core.domain"
            }
            .buildConsumerProperties()
    }

    @Bean
    open fun userConsumerTemplate(): ReactiveKafkaConsumerTemplate<String, UserMessage> {
        return userConsumerConfigs()
            .let { ReceiverOptions.create<String, UserMessage>(it).subscription(listOf(userTopicString)) }
            .let { ReactiveKafkaConsumerTemplate(it) }
    }

    @Bean
    open fun orderProducerTemplate(): ReactiveKafkaProducerTemplate<String, OrderMessage> {
        return producerConfigs()
            .let { SenderOptions.create<String, OrderMessage>(it) }
            .let { ReactiveKafkaProducerTemplate(it) }
    }

    @Bean
    open fun userKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, UserMessage> {
        val jsonDeserializer = JsonDeserializer<UserMessage>()
        jsonDeserializer.addTrustedPackages("io.violabs.core.domain")

        val keySupplier: Supplier<Deserializer<String>> = Supplier { StringDeserializer() }
        val valueSupplier: Supplier<Deserializer<UserMessage>> = Supplier { jsonDeserializer }

        val consumerFactory = DefaultKafkaConsumerFactory(userConsumerConfigs(), keySupplier, valueSupplier)
        val producerFactory = DefaultKafkaProducerFactory<String, Any>(producerConfigs())
        val kafkaTemplate = KafkaTemplate(producerFactory)
        val deadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer)

        return ConcurrentKafkaListenerContainerFactory<String, UserMessage>()
            .also { it.consumerFactory = consumerFactory }
            .also { it.setCommonErrorHandler(errorHandler) }
    }
}