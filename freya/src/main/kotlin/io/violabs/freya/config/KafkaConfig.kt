package io.violabs.freya.config

import io.violabs.freya.domain.AppUser
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
    lateinit var bootstrapServers: String

    @Bean
    open fun producerConfigs(): Map<String, Any> {
        return KafkaProperties()
            .also {
                val producer = it.producer

                producer.bootstrapServers = bootstrapServers.split(",").toList()
                producer.keySerializer = StringSerializer::class.java
                producer.valueSerializer = JsonSerializer::class.java
            }
            .buildProducerProperties()
    }

    @Bean
    open fun consumerConfigs(): Map<String, Any> {
        return KafkaProperties()
            .also {
                val consumer = it.consumer

                consumer.bootstrapServers = bootstrapServers.split(",").toList()
                consumer.keyDeserializer = StringDeserializer::class.java
                consumer.valueDeserializer = JsonSerializer::class.java
                consumer.groupId = "json"
            }
            .buildConsumerProperties()
    }

    @Bean
    open fun producerTemplate(): ReactiveKafkaProducerTemplate<String, Any>? {
        return producerConfigs()
            .let { SenderOptions.create<String, Any>(it) }
            .let { ReactiveKafkaProducerTemplate(it) }
    }

    @Bean
    open fun consumerTemplate(): ReactiveKafkaConsumerTemplate<String, Any>? {
        return consumerConfigs()
            .let { ReceiverOptions.create<String, Any>(it) }
            .let { ReactiveKafkaConsumerTemplate(it) }
    }

    @Bean
    open fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, AppUser>? {
        val jsonDeserializer = JsonDeserializer<AppUser>()
        jsonDeserializer.addTrustedPackages("io.violabs.freya.domain")

        val keySupplier: Supplier<Deserializer<String>> = Supplier { StringDeserializer() }
        val valueSupplier: Supplier<Deserializer<AppUser>> = Supplier { jsonDeserializer }

        val consumerFactory = DefaultKafkaConsumerFactory(consumerConfigs(), keySupplier, valueSupplier)
        val producerFactory = DefaultKafkaProducerFactory<String, Any>(producerConfigs())
        val kafkaTemplate = KafkaTemplate(producerFactory)
        val deadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer)

        return ConcurrentKafkaListenerContainerFactory<String, AppUser>()
            .also { it.consumerFactory = consumerFactory }
            .also { it.setCommonErrorHandler(errorHandler) }
    }
}