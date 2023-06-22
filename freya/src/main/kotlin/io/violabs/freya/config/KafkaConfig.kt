package io.violabs.freya.config

import io.violabs.core.domain.UserMessage
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.sender.SenderOptions

@Configuration
open class KafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers:localhost:29092,localhost:39092}")
    lateinit var bootstrapServers: String

    @Bean
    open fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        return KafkaAdmin(configs)
    }

    @Bean
    open fun userTopic(): NewTopic {
        return NewTopic("user", 1, 1.toShort())
    }

    @Bean
    open fun userProducerConfigs(): Map<String, Any> {
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

//    @Bean
//    open fun consumerConfigs(): Map<String, Any> {
//        return KafkaProperties()
//            .also {
//                val consumer = it.consumer
//
//                consumer.bootstrapServers = bootstrapServers.split(",").toList()
//                consumer.keyDeserializer = StringDeserializer::class.java
//                consumer.valueDeserializer = JsonDeserializer::class.java
//                consumer.groupId = "json"
//                consumer.autoOffsetReset = "earliest"
//                consumer.properties["spring.json.trusted.packages"] = "io.violabs.freya.domain"
//            }
//            .buildConsumerProperties()
//    }

    @Bean
    open fun producerTemplate(): ReactiveKafkaProducerTemplate<String, UserMessage> {
        return userProducerConfigs()
            .let { SenderOptions.create<String, UserMessage>(it) }
            .let { ReactiveKafkaProducerTemplate(it) }
    }

//    @Bean
//    open fun consumerTemplate(): ReactiveKafkaConsumerTemplate<String, AppUser> {
//        return consumerConfigs()
//            .let { ReceiverOptions.create<String, AppUser>(it).subscription(listOf("user")) }
//            .let { ReactiveKafkaConsumerTemplate(it) }
//    }

//    @Bean
//    open fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, AppUser> {
//        val jsonDeserializer = JsonDeserializer<AppUser>()
//        jsonDeserializer.addTrustedPackages("io.violabs.freya.domain")
//
//        val keySupplier: Supplier<Deserializer<String>> = Supplier { StringDeserializer() }
//        val valueSupplier: Supplier<Deserializer<AppUser>> = Supplier { jsonDeserializer }
//
//        val consumerFactory = DefaultKafkaConsumerFactory(consumerConfigs(), keySupplier, valueSupplier)
//        val producerFactory = DefaultKafkaProducerFactory<String, Any>(producerConfigs())
//        val kafkaTemplate = KafkaTemplate(producerFactory)
//        val deadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
//        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer)
//
//        return ConcurrentKafkaListenerContainerFactory<String, AppUser>()
//            .also { it.consumerFactory = consumerFactory }
//            .also { it.setCommonErrorHandler(errorHandler) }
//    }
}