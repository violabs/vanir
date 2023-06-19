import io.violabs.freya.domain.AppUser
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate

@TestConfiguration
open class KafkaTestConfig {

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    open fun userKafkaConsumer(consumerTemplate: ReactiveKafkaConsumerTemplate<String, AppUser>): KafkaConsumer {
        return KafkaConsumer(consumerTemplate)
    }

    class KafkaConsumer(private val consumerTemplate: ReactiveKafkaConsumerTemplate<String, AppUser>) {
        suspend fun consume(): AppUser? {
            return consumerTemplate
                .receive()
                .awaitFirstOrNull()
                ?.value()
                ?.also { println("Received user: $it") }
                ?: run { println("No user received"); null }
        }
    }
}